package com.nitish.gamershub.utils;

public abstract class NetworkResponse<T> {


    public static class Success<T> extends NetworkResponse<T> {
        public T data = null;

        public Success(T data) {

            this.data = data;
        }

        public T getData() {
            return data;
        }
    }
    public static class Error<T> extends NetworkResponse<T>
    {
        public String message;
        public Error(String message){
            this.message = message;
        }
        public  String getMessage()
        {
            return  this.message;
        }
    }
    public static class Loading<T> extends NetworkResponse<T> {


     public Loading(){
        }


        public static final Loading<?> INSTANCE = new Loading<>();



    }
}

