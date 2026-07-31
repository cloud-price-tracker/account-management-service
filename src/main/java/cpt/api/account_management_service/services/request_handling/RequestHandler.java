package cpt.api.account_management_service.services.request_handling;

public interface RequestHandler<REQ, RES> {
    RES handle(REQ request);
}