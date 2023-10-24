package com.nitish.gamershub.utils;

public abstract class NetworkResponse<T> {


    public static class Success<T> extends NetworkResponse<T> {
        private T data = null;
        private String message = null;

        public Success(T data) {

            this.data = data;
        }

        public Success(T data, String message) {

            this.message = message;
            this.data = data;
        }

        public T getData() {
            return data;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class Error<T> extends NetworkResponse<T> {
        private String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public static class Loading<T> extends NetworkResponse<T> {


        public Loading() {
        }


        public static final Loading<?> INSTANCE = new Loading<>();


    }
}

