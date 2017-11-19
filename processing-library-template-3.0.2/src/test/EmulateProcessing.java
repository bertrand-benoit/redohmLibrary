package test;

import processing.core.PApplet;
import processing.core.PShape;
import processing.serial.*;
import fr.redohm.utils.RedohmCommunicationUtils;
import fr.redohm.parsing.*;


public class EmulateProcessing extends PApplet {
	// Definition du port cible, en minuscules.
	static String ARDUINO_PORT = "com6";
	static int SCALE = 2;

	// TODO: complete Derive offset => sensitivity to ignore move, to avoid derive ...
	static float DERIVE_OFFSET = 0.1f;
	
	Serial myPort;
	RedohmDataParser dataParser;
	RedohmCommunicationUtils communicationUtils;
	PShape spacecraft;
	
	float initialRateX = 0.0f, initialRateY = 0.0f, initialRateZ = 0.0f;
	float previousAngleX = 0.0f, previousAngleY = 0.0f, previousAngleZ = 0.0f;
	float previousTime = millis();

	public static void main(String[] args) {
		PApplet.main("test.EmulateProcessing");

	}
	
    public void settings(){
    	  size(1024, 1000); //, P3D);
    }

    public void setup(){
        //fill(120,50,240);
        

        // Instantiation de la classe utilitaire de communication Redohm. 
        communicationUtils = new RedohmCommunicationUtils();
       
        // Verification de l'existence du port cible.
        if (!communicationUtils.checkSerialPort(ARDUINO_PORT)) {
          println("Impossible de trouver le port '" + ARDUINO_PORT + "'. Veuillez v�rifier la configuration et la bonne connexion.");
          // Demande de sortie (diff�r�e) du programme.
          exit();
          // Sortie imm�diate du setup().
          return;
        }
        
        // Initialisation concr�te du port sur lequel communiquer avec l'Arduino.
        println("Initialisation du port '" + ARDUINO_PORT + "'.");
        myPort = new Serial(this, ARDUINO_PORT, 9600);
        myPort.bufferUntil('\n');
        
        // D�finie le mod�le de donn�es � g�rer.
        RedohmDataInfo accelXModel = new RedohmDataInfo("accelx", "float", -25055, 32754, -180, 180);
        RedohmDataInfo accelYModel = new RedohmDataInfo("accely", "float", -28582, 18138, -180, 180);
        RedohmDataInfo accelZModel = new RedohmDataInfo("accelz", "float", -24770, 18206, -180, 180);
        RedohmDataInfo gyroXModel = new RedohmDataInfo("gyrox", "float", 0, 2000, -180, 180);
        RedohmDataInfo gyroYModel = new RedohmDataInfo("gyroy", "float", 0, 2000, -180, 180);
        RedohmDataInfo gyroZModel = new RedohmDataInfo("gyroz", "float", 0, 2000, -180, 180);
        RedohmDataInfo tempCModel = new RedohmDataInfo("tempc", "float", -24770, 18206, -180, 180);
        RedohmDataInfo tempFModel = new RedohmDataInfo("tempf", "float", -24770, 18206, -180, 180);
        RedohmDataModel dataModel = new RedohmDataModel(accelXModel, accelYModel, accelZModel, gyroXModel, gyroYModel, gyroZModel, tempCModel, tempFModel);   
        dataParser = new RedohmDataParser(dataModel, ";", myPort);
        
        // On fait une moyenne sur les premi�res valeurs, pour d�finir la valeur initial pour chaque angle.
        println("Calibrage du syst�me, le capteur doit rester immobile ...");
        dataParser.readNextValues(1);
        initialRateX = dataParser.getDataValue("gyrox");
        initialRateY = dataParser.getDataValue("gyroy");
        initialRateZ = dataParser.getDataValue("gyroz");
        
        println("Valeurs initiales des acc�l�rations : initialRateX=" + initialRateX + "; initialRateY=" + initialRateY + "; initialRateZ=" + initialRateZ);
        previousTime = millis();
        println("Chargement du mod�le 3D ...");  
        
        //spacecraft = loadShape("rocket.obj");
        spacecraft = loadShape("star-trek-sovereign-class-ussgeblendert.obj");
    }

    public void draw(){

    	  // V�rification de la disponibilit� du port initialis�.
    	  if (myPort.available() > 0)
    	  {
    	    // Demande le lissage sur les prochaines valeurs.
    	    dataParser.readNextValues(4);

    	    // R�cup�ration des valeurs mapp�es.
    	    float rateX = dataParser.getDataValue("gyrox");
    	    float rateY = dataParser.getDataValue("gyroy");
    	    float rateZ = dataParser.getDataValue("gyroz");
    	    
    	    // Calcul d'int�gration sur les valeurs angulaires, afin de d�terminer les angles de rotations (aussi appel�s Angles Euler).
    	    float currentTime = millis(); 
    	    float timeDiffInSeconds = (currentTime - previousTime)/1000;
    	    float angleX = (rateX - initialRateX) * timeDiffInSeconds + previousAngleX;
    	    float angleY = (rateY - initialRateY) * timeDiffInSeconds + previousAngleY;
    	    float angleZ = (rateZ - initialRateZ) * timeDiffInSeconds + previousAngleZ;

    	    // Syst�me anti-d�rive, pour �viter de l�gers mouvements inutiles, du � la sensibilit� du capteur (� terminer).
    	    angleX = (abs(angleX - previousAngleX) < DERIVE_OFFSET) ? previousAngleX : angleX;
    	    angleY = (abs(angleY - previousAngleY) < DERIVE_OFFSET) ? previousAngleY : angleY;
    	    angleZ = (abs(angleZ - previousAngleZ) < DERIVE_OFFSET) ? previousAngleZ : angleZ;
    	    
    	    // TODO: correction & mapping 0 / 2PI ou �quivalent ...   
    	    
    	    
    	    println("Nouvelles valeurs d'angles (Temps en secondes, entre les 2 mesures=" + timeDiffInSeconds + ") : angleX=" + angleX + "; angleY=" + angleY + "; angleZ=" + angleZ);
    	 
    	    background(0);    
    	    
    	    //camera(x, y, z / tan(PI/6), width/2, height/2, 0, 1, 0, 0);
    	    translate(width/2, height/2);

    	    shapeMode(CENTER);
    	    scale(64);
    	    shape(spacecraft);
    	    // Rotates a single form like triangle ...
    	    rotateX(-radians(angleX-75));
    	    rotateY(radians(angleY));
    	    rotateZ(-radians(angleZ));
    	  
    	    triangle(-100 * SCALE, 0 * SCALE, 100 * SCALE, 0 * SCALE, 0 * SCALE, -100 * SCALE);
    	    noStroke();
    	    colorMode(RGB, 100);
    	    for (int i = 25; i < 75; i++) {
    	      for (int j = 0; j < 50 * SCALE; j++) {
    	        stroke(i, j, 100);
    	        point((-100+i) * SCALE, j);
    	        point((100-i) * SCALE, j);
    	      }
    	    }
    	    
    	    //stroke(128);
    	    ////noFill();
    	    //box(80,240,20);
    	    
    	    //fill(0);
    	    
    	    // Enregistrements pour l'it�ration suivante.
    	    previousAngleX = angleX;
    	    previousAngleY = angleY;
    	    previousAngleZ = angleZ;
    	    previousTime = currentTime; // TODO: don't do that if anti-derive system has been used ...
    	  }
    }

}
