package org.openjfx;

import javafx.concurrent.Task;

public class ThreadEx extends Task<Void> {




    @Override
    protected Void call() throws Exception {
        try {
            Thread.sleep(3000);


        } catch (InterruptedException e) {
            // gjør ikke noe her, men hvis hvis du er i en løkke
            // burde løkkes stoppes med break hvis isCancelled() er true...
        }

        return null;
    }

}
