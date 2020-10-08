package com.devej.matchingcard;

public class CardInfo {
    public CardInfo(int frontImage, Boolean cardOpened) {
        this.frontImage = frontImage;
        this.cardOpened = cardOpened;
    }

    int frontImage;
    Boolean cardOpened;

    public int getFrontImage() {
        return frontImage;
    }

    public void setFrontImage(int frontImage) {
        this.frontImage = frontImage;
    }

    public Boolean getCardOpened() {
        return cardOpened;
    }

    public void setCardOpened(Boolean cardOpened) {
        this.cardOpened = cardOpened;
    }
}
