package controller;

import java.util.List;

import exceptions.AuthorizationException;
import exceptions.DatabaseException;
import exceptions.EntityNotFoundException;
import exceptions.ValidationException;
import model.AppModel;
import model.CaseRecord;

public class PoliceController {

    private static AppModel appModel;

    public static void initialize(AppModel model) {
        appModel = model;
    }

    public static void createCase(
            String caseId,
            String description,
            String victimUsername,
            String accusedUsername)
            throws AuthorizationException,
                   ValidationException,
                   DatabaseException {

        appModel.createCase(
                LoginController.getCurrentUser(),
                caseId,
                description,
                victimUsername,
                accusedUsername
        );
    }

    public static void updateCase(
            String caseId,
            String description,
            CaseRecord.Status status)
            throws AuthorizationException,
                   ValidationException,
                   EntityNotFoundException,
                   DatabaseException {

        appModel.updateCase(
                LoginController.getCurrentUser(),
                caseId,
                description,
                status
        );
    }

    public static List<CaseRecord> viewCases()
            throws AuthorizationException, DatabaseException {

        return appModel.getVisibleCases(
                LoginController.getCurrentUser()
        );
    }
}