package com.cowards.onlyarts.repositories.artwork;

public class ArtworkERROR extends Exception {

    public ArtworkERROR(String message) {
        super(message);
    }

    public ArtworkERROR(String message, Throwable cause) {
        super(message, cause);
    }
}
