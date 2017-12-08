package com.manywho.sharepoint.flow.addin.hosted.flow;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.manywho.sdk.api.InvokeType;
import com.manywho.sdk.api.draw.flow.FlowId;
import com.manywho.sdk.api.run.*;
import com.manywho.sdk.api.security.AuthenticationCredentials;
import com.manywho.sdk.client.run.RunClient;

import javax.ws.rs.*;
import java.util.UUID;

@Path("/callback")
public class AddInController {

    private RunClient runClient;

    @Inject
    public AddInController(RunClient runClient) {

        this.runClient = runClient;
    }

    @Path("/run-flow-web-part")
    @Produces("text/html")
    @POST
    public String runFlowWebPart(@FormParam("SPAppToken") String contextToken,
                                 @QueryParam("editmode") String editMode,
                                 @QueryParam("flow-id") String flowId,
                                 @QueryParam("flow-version-id") String flowVersionId,
                                 @QueryParam("tenant-id") String tenantId,
                                 @QueryParam("admin-tenant-id") String adminTenantId,
                                 @QueryParam("host") String host,
                                 @QueryParam("player") String player,
                                 @QueryParam("mode") String mode) {

        return this.runFlowInternal(contextToken, flowId, flowVersionId, tenantId, adminTenantId, host, player, mode);
    }

    @Path("/run-flow")
    @Produces("text/html")
    @POST
    public String runFlow(@FormParam("SPAppToken") String contextToken) {

        // todo allow configure the app to run standalone without a web part
        return this.runFlowInternal(contextToken, "", "", "", "", "",
                "", "");
    }

    @Path("/run-flow")
    @Produces("text/html")
    @GET
    public String runFlowGet() {

        return runFlowInternal("", "", "", "", "", "", "", "");
        // todo allow configure the app to run standalone without a web part
    }

    private String runFlowInternal(String contextToken, String flowId, String flowVersionId, String tenantId,
                                   String adminTenantId, String host, String player, String mode) {

        String joinUrl = "";

        UUID tenantUuid = null;
        UUID flowIdUuid = null;
        UUID flowVersionIdUuid = null;

        try {

            if (!Strings.isNullOrEmpty(flowId)) {
                flowIdUuid = UUID.fromString(flowId);
            }

            if (!Strings.isNullOrEmpty(flowVersionId)) {
                flowVersionIdUuid = UUID.fromString(flowVersionId);
            }

            if (!Strings.isNullOrEmpty(tenantId)) {
                tenantUuid = UUID.fromString(tenantId);
            }

            EngineInitializationRequest request = new EngineInitializationRequest();
            FlowId flowIdrequest = new FlowId();
            flowIdrequest.setId(flowIdUuid);
            flowIdrequest.setVersionId(flowVersionIdUuid);

            request.setFlowId(flowIdrequest);
            request.setPlayerUrl("https://" + host + "/" + tenantId + "/play/" + player);
            request.setJoinPlayerUrl("https://" + host + "/" + tenantId + "/play/" + player);

            EngineInitializationResponse engineInitializationResponse = runClient.initialize(null, tenantId, request)
                    .execute()
                    .body();

            EngineInvokeRequest engineInvokeRequest = new EngineInvokeRequest();
            engineInvokeRequest.setStateId(engineInitializationResponse.getStateId());
            engineInvokeRequest.setInvokeType(InvokeType.Sync);
            engineInvokeRequest.setStateToken(engineInitializationResponse.getStateToken());
            engineInvokeRequest.setMode(mode);
            engineInvokeRequest.setCurrentMapElementId(engineInitializationResponse.getCurrentMapElementId());

            AuthenticationCredentials authenticationCredentials = new AuthenticationCredentials();
            authenticationCredentials.setSessionToken(contextToken);
            authenticationCredentials.setTenantId(tenantUuid);

            String authentication = runClient.authentication(tenantUuid, engineInitializationResponse.getStateId(),
                    authenticationCredentials)
                    .execute()
                    .body();

            EngineInvokeResponse engineInvokeResponse1 = runClient.join(authentication, tenantUuid, engineInitializationResponse.getStateId())
                    .execute()
                    .body();

            joinUrl = engineInvokeResponse1.getJoinFlowUri();

        } catch (Exception e) {
            joinUrl = "https://flow.manywho.com/bb03e922-8a39-46e8-b492-aacd2ccb5a42/play/default?join=f59b6c8d-4892-4cce-87b7-297cd0440601";
        }

        //return template;
        String ifFrame = "<iframe  src=\"" + joinUrl + "\" frameborder=\"0\" style=\"overflow:hidden;height:calc(100vh - 300px);width:100%\" height=\"100%\" width=\"100%\"></iframe>";
        return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>title</title></head><body>" + ifFrame + "</body></html>";

    }
}
