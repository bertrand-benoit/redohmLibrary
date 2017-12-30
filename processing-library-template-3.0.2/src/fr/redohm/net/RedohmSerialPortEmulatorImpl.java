package fr.redohm.net;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.redohm.RedohmLibraryException;
import fr.redohm.utils.RedohmLogUtils;
import fr.redohm.utils.RedohmUtils;

public final class RedohmSerialPortEmulatorImpl implements IRedohmSerialPort {

    private String parentDirectoryPath = null;

    private final List<Path> samplingFileList;
    private final int delayInMilliSeconds;
    private Iterator<Path> samplingFileListIterator;

    private List<String> currentSamplingLines = null;

    public RedohmSerialPortEmulatorImpl() {
        samplingFileList = new ArrayList<Path>();

        String definedDelay = System.getProperty(RedohmUtils.SAMPLING_EMULATION_DELAY_MILLI, "0");
        int parsedDelayInMilliSeconds;
        try {
            parsedDelayInMilliSeconds = Integer.parseInt(definedDelay);
        } catch (NumberFormatException e) {
            RedohmLogUtils.logError("Specified SAMPLING_EMULATION_DELAY_MILLI '" + definedDelay
                    + "' is NOT a number ! Disabling Emulation sampling delay.");
            parsedDelayInMilliSeconds = 0;
        }
        delayInMilliSeconds = parsedDelayInMilliSeconds;
    }

    @Override
    public final void initialize() throws RedohmLibraryException {
        // Ensures configuration is OK.
        parentDirectoryPath = System.getProperty(RedohmUtils.SAMPLING_EMULATION_DIR_PROPERTY);
        if (parentDirectoryPath == null)
            throw new IllegalArgumentException("You must define '" + RedohmUtils.SAMPLING_EMULATION_DIR_PROPERTY
                    + "' System property to use Sampling system emulator.");

        // Ensures specified directory exists.
        Path samplingDirectory = Paths.get(parentDirectoryPath);
        if (!Files.exists(samplingDirectory))
            throw new IllegalArgumentException("The '" + RedohmUtils.SAMPLING_EMULATION_DIR_PROPERTY
                    + "' System property is defined to '" + samplingDirectory + "' which does not exist.");

        // N.B.: does not use/register the DirectoryStream itself because there is no
        // proper way to close it when
        // Processing execution is completed.
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(samplingDirectory, "*.txt")) {
            for (Path entry : stream) {
                samplingFileList.add(entry);
                RedohmLogUtils.logDebug("Registered new available sampling file '" + entry.getFileName() + "'.");
            }
        } catch (IOException e) {
            throw new RedohmLibraryException("Error while initializing RedohmSerialPortEmulatorImpl; with the '"
                    + RedohmUtils.SAMPLING_EMULATION_DIR_PROPERTY + "' System property is defined to '"
                    + samplingDirectory + "' which does not exist.", e);
        }

        // Initializes the iterator.
        samplingFileListIterator = samplingFileList.iterator();
    }

    @Override
    public final boolean available() {
        // This emulator is ALWAYS available.
        return true;
    }

    @Override
    public final String readNextLine() throws RedohmLibraryException {
        // Initializes if needed.
        try {
            if (currentSamplingLines == null || currentSamplingLines.isEmpty()) {
                if (!samplingFileListIterator.hasNext()) {
                    RedohmLogUtils.logWarning("There is NO more sampling files ... Nothing more can be emulated");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        RedohmLogUtils.logException(
                                "Exception occurs while sleeping because there is no more sampling files to manage.",
                                e);
                    }
                    return null;
                }

                Path nextSamplingFile = samplingFileListIterator.next();
                RedohmLogUtils
                        .logMessage("Emulation system will now use the sampling file '" + nextSamplingFile + "'.");
                currentSamplingLines = Files.readAllLines(nextSamplingFile);
            }
        } catch (IOException e) {
            throw new RedohmLibraryException("Error while reading sampling file.", e);
        }

        // Emulates sampling delay.
        if (delayInMilliSeconds > 0) {
	        try {
	            Thread.sleep(delayInMilliSeconds);
	        } catch (InterruptedException e) {
	            RedohmLogUtils.logException("Exception occurs while emulating sampling delay ...", e);
	        }
        }
        return currentSamplingLines.remove(0);
    }

}
