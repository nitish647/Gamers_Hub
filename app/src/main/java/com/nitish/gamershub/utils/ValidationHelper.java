package com.nitish.gamershub.utils;

import android.content.Context;
import android.util.Patterns;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationHelper {


    // will check the given edittext is filled or not
    // will return true if the edittext is empty
    public static boolean isFilled(EditText editText) {
        boolean isFilled;
        if (editText.getText().toString().trim().isEmpty()) {
            // if not filled then show the error message and request the focus
            editText.setError("Required Field");
            editText.requestFocus();
            isFilled = false; // if the edi
        } else
            isFilled = true;

        return isFilled;
    }


    public static String getPhoneWithoutCountryCode(String phone) {

        // this function is to remove the country code from a phone number

        if (phone.trim().length() > 10) {
            // remove the +91
            if (phone.contains("+91")) {
                phone = phone.replace("+91", "");
            }
            // remove the fist 91
            else if (phone.contains("91")) {
                phone = phone.replaceFirst("91", "");
            }

        }

        return phone;
    }

    // mail validator
    public static boolean isValidMail(String email) {
        //Regular Expression
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        //Compile regular expression to get the pattern
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();// mather matched the pattern and return the true or false
    }
    //function to check if the mobile number is valid or not  (Indian phone number)

    public static boolean isValidUpiId(String upi) {
        Pattern patterns = Pattern.compile("[a-zA-Z0-9.\\-_]{2,49}@[a-zA-Z._]{2,49}");
        Matcher matcher = patterns.matcher(upi);
        return (matcher.find() && matcher.group().equals(upi));
    }

    public static boolean isValidIndianMobileNo(String str) {
//(0/91): number starts with (0/91)
//[7-9]: starting of the number may contain a digit between 0 to 9
//[0-9]: then contains digits 0 to 9
        Pattern ptrn = Pattern.compile("(0/91)?[6-9][0-9]{9}");
//the matcher() method creates a matcher that will match the given input against this pattern
        Matcher match = ptrn.matcher(str);
//returns a boolean value
        return (match.find() && match.group().equals(str));
    }

    public static boolean validatePhone(EditText editText, Context context) {
        String phoneInput = editText.getText().toString().trim();
        if (phoneInput.isEmpty()) {


            return false;
        } else if (!Patterns.PHONE.matcher(phoneInput).matches()) {

            return false;
        } else if (phoneInput.length() < 10) {

            return false;
        } else {
            return true;
        }

    }
}
