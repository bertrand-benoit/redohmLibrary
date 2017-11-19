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
          println("Impossible de trouver le port '" + ARDUINO_PORT + "'. Veuillez vérifier la configuration et la bonne connexion.");
          // Demande de sortie (différée) du programme.
          exit();
          // Sortie immédiate du setup().
          return;
        }
        
        // Initialisation concrète du port sur lequel communiquer avec l'Arduino.
        println("Initialisation du port '" + ARDUINO_PORT + "'.");
        myPort = new Serial(this, ARDUINO_PORT, 9600);
        myPort.bufferUntil('\n');
        
        // Définie le modèle de données à gérer.
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
        
        // On fait une moyenne sur les premières valeurs, pour définir la valeur initial pour chaque angle.
        println("Calibrage du système, le capteur doit rester immobile ...");
        dataParser.readNextValues(1);
        initialRateX = dataParser.getDataValue("gyrox");
        initialRateY = dataParser.getDataValue("gyroy");
        initialRateZ = dataParser.getDataValue("gyroz");
        
        println("Valeurs initiales des accélérations : initialRateX=" + initialRateX + "; initialRateY=" + initialRateY + "; initialRateZ=" + initialRateZ);
        previousTime = millis();
        println("Chargement du modèle 3D ...");  
        
        //spacecraft = loadShape("rocket.obj");
        spacecraft = loadShape("star-trek-sovereign-class-ussgeblendert.obj");
    }

    public void draw(){

    	  // Vérification de la disponibilité du port initialisé.
    	  if (myPort.available() > 0)
    	  {
    	    // Demande le lissage sur les prochaines valeurs.
    	    dataParser.readNextValues(4);

    	    // Récupération des valeurs mappées.
    	    float rateX = dataParser.getDataValue("gyrox");
    	    float rateY = dataParser.getDataValue("gyroy");
    	    float rateZ = dataParser.getDataValue("gyroz");
    	    
    	    // Calcul d'intégration sur les valeurs angulaires, afin de déterminer les angles de rotations (aussi appelés Angles Euler).
    	    float currentTime = millis(); 
    	    float timeDiffInSeconds = (currentTime - previousTime)/1000;
    	    float angleX = (rateX - initialRateX) * timeDiffInSeconds + previousAngleX;
    	    float angleY = (rateY - initialRateY) * timeDiffInSeconds + previousAngleY;
    	    float angleZ = (rateZ - initialRateZ) * timeDiffInSeconds + previousAngleZ;

    	    // Système anti-dérive, pour éviter de légers mouvements inutiles, du à la sensibilité du capteur (à terminer).
    	    angleX = (abs(angleX - previousAngleX) < DERIVE_OFFSET) ? previousAngleX : angleX;
    	    angleY = (abs(angleY - previousAngleY) < DERIVE_OFFSET) ? previousAngleY : angleY;
    	    angleZ = (abs(angleZ - previousAngleZ) < DERIVE_OFFSET) ? previousAngleZ : angleZ;
    	    
    	    // TODO: correction & mapping 0 / 2PI ou équivalent ...   
    	    
    	    
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
    	    
    	    // Enregistrements pour l'itération suivante.
    	    previousAngleX = angleX;
    	    previousAngleY = angleY;
    	    previousAngleZ = angleZ;
    	    previousTime = currentTime; // TODO: don't do that if anti-derive system has been used ...
    	  }
    }

}
