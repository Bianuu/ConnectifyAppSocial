package com.example.disiprojectbackend.validators;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserValidator {
    public static List<String> validateWholeDataForUser(String email, String password, String username, String bio, Date createdAt, Date updatedAt, Date dateOfBirth) {
        List<String> errorMessages = new ArrayList<>();

        validateEmail(email, errorMessages);
        validatePassword(password, errorMessages);
        validateUsername(username, errorMessages);
        validateBio(bio, errorMessages);
        validateCreatedAt(createdAt, errorMessages);
        validateUpdatedAt(updatedAt, errorMessages);
        validateDateOfBirth(dateOfBirth, errorMessages);

        return errorMessages;
    }

    public static void validateEmail(String email, List<String> errorMessages) {
        if (email == null || email.isEmpty()) {
            errorMessages.add("Email cannot be empty");
        } else if (!email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")) {
            errorMessages.add("Invalid email format");
        }
    }

    public static void validatePassword(String password, List<String> errorMessages) {
        if (password.length() < 7 || password.length() > 12) {
            errorMessages.add("Password must be between 7 and 12 characters");
        }
    }

    public static void validateUsername(String username, List<String> errorMessages) {
        if (username.length() == 0) {
            errorMessages.add("Invalid username format");
        }
    }

    public static void validateBio(String bio, List<String> errorMessages) {
        if (bio.length() == 0) {
            errorMessages.add("Invalid bio format");
        }
    }

    public static void validateCreatedAt(Date createdAt, List<String> errorMessages) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(createdAt);

        Calendar currentCalendar = Calendar.getInstance();

        if (calendar.get(Calendar.YEAR) != currentCalendar.get(Calendar.YEAR) ||
                calendar.get(Calendar.DAY_OF_YEAR) != currentCalendar.get(Calendar.DAY_OF_YEAR)) {
            errorMessages.add("Data creată nu este din ziua curentă.");
        }
    }

    public static void validateUpdatedAt(Date updatedAt, List<String> errorMessages) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(updatedAt);

        Calendar currentCalendar = Calendar.getInstance();

        if (calendar.get(Calendar.YEAR) != currentCalendar.get(Calendar.YEAR) ||
                calendar.get(Calendar.DAY_OF_YEAR) != currentCalendar.get(Calendar.DAY_OF_YEAR)) {
            errorMessages.add("Data creată nu este din ziua curentă.");
        }
    }


    public static void validateDateOfBirth(Date dateOfBirth, List<String> errorMessages) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -14);
        Date minDate = calendar.getTime();
        if (dateOfBirth.after(minDate)) {
            errorMessages.add("You must be at least 14 years old");
        }
    }
}