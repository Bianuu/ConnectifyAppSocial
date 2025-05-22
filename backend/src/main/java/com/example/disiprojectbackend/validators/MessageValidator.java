package com.example.disiprojectbackend.validators;

import java.util.ArrayList;
import java.util.List;

public class MessageValidator {

    public static List<String> validateWholeDataForPost(String content) {
        List<String> errorMessages = new ArrayList<>();

        validateContent(content, errorMessages);

        return errorMessages;
    }

    public static void validateContent(String content, List<String> errorMessages) {
        if (content == null || content.trim().isEmpty()) {
            errorMessages.add("Content cannot be empty.");
        }
    }
}
