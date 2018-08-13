package com.ezeia.politicalparty.model;

public class Error {
    private String errorTitle;
    private String errorMessage;
    private Integer errorImage;
    private String errorCode;

    public String getErrorTitle() {
        return errorTitle;
    }

    public void setErrorTitle(String errorTitle) {
        this.errorTitle = errorTitle;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getErrorImage() {
        return errorImage;
    }

    public void setErrorImage(int errorImage) {
        this.errorImage = errorImage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
