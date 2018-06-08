/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myLibrary.javafx.LoadingUtils;

import javafx.concurrent.Task;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Alejandro
 */
public class LoadingStage extends Stage {
    
    private final LoadingScene scene;
    //Whether the stage should hide automatically when the loading service ends
    private boolean implicitHiding = true;
    
    //Constructors
    public LoadingStage(Task... tasks) {
        this(LoadingScene.DEF_WIDTH, LoadingScene.DEF_HEIGHT, tasks);
    }
    public LoadingStage(double width, double height, Task... tasks) {
        this(new LoadingScene(width, height, tasks));
    }
    public LoadingStage(LoadingScene s) {
        super(StageStyle.UNDECORATED);
        scene = s;
        setTitle("Loading...");
        setWidth(scene.getWidth()); setHeight(scene.getHeight());
        initModality(Modality.APPLICATION_MODAL);
        setScene(scene);
        
        //If the user tries to close the stage, it cancels the main task
        setOnCloseRequest(e -> { 
            scene.loadingQueue.cancel();
            e.consume(); //This will prevent the window from closing until the task has been successfully cancelled
        });
        
        //If all tasks ended and the implicitHiding value is set to true, hide the stage
        scene.loadingQueue.main.runningProperty().addListener((obs, wasRunning, isRunning) -> {
            if (!isRunning && implicitHiding) hide();
        });
    }
    
    //Getting the queue
    public LoadingQueue getQueue() { return scene.getQueue(); }
    
    //Getting if tasks succeeded or not
    public boolean waitFor() { return scene.waitFor(); }
    public boolean waitFor(long timeout) { return scene.waitFor(timeout); }
    public boolean isSucceeded() { return scene.isSucceeded(); }
    
    public void setImplicitHiding(boolean isHiding) {
        implicitHiding = isHiding;
        if (implicitHiding && !scene.loadingQueue.main.isRunning()) hide();
    }
}
