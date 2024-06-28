package controllers.api.onboarding

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs.{Consumes, GET, POST, Path, Produces}
import jakarta.ws.rs.core.MediaType
import models.Response
import play.api.libs.Files
import play.api.libs.json.JsObject
import play.api.mvc.{Action, AnyContent, MultipartFormData}
import services.account.AccountException
import services.onboarding.dto.requests.{ProfileRequest, WorkspaceRequest}
import services.onboarding.dto.responses.UserOnboardingResponse
import services.workspaces.WorkspaceException

trait OnboardingController {

    @Path("profile")
    @POST
    @Consumes(Array(MediaType.MULTIPART_FORM_DATA))
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = "Update your accounts profile",
        tags = Array("onboarding"),
        requestBody = new RequestBody(
            description = "Will receive an updated account data with a profile image and it will save it",
            required = true,
            content = Array(
                new Content(schema = new Schema(implementation = classOf[ProfileRequest]))
            )
        ),
        responses = Array(
            new ApiResponse(
                description = "In case everything went well, will have update the profile key",
                responseCode = "200",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[Response]))
                )
            ),
            new ApiResponse(
                description = "In case the user isn't found, it will return a not found code with an exception",
                responseCode = "404",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[AccountException]))
                )
            ),
            new ApiResponse(
                description = "In case theres no valid JWT Token it will return an error",
                responseCode = "401",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[JsObject]))
                )
            ),
            new ApiResponse(
                description = "In case something unknown happens, it will return a 500 error",
                responseCode = "500",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[AccountException]))
                )
            )
        )
    )
    def saveProfile: Action[MultipartFormData[Files.TemporaryFile]]

    @Path("Workspace")
    @POST
    @Consumes(Array(MediaType.MULTIPART_FORM_DATA))
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = "Create a workspace",
        tags = Array("Onboarding"),
        requestBody = new RequestBody(
            description = "Will receive some info to create a workspace",
            required = true,
            content = Array(
                new Content(schema = new Schema(implementation = classOf[WorkspaceRequest]))
            )
        ),
        responses = Array(
            new ApiResponse(
                description = "In case everything went well, it will return the Workspace key",
                responseCode = "200",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[Response]))
                )
            ),
            new ApiResponse(
                description = "In case the account associated with the workspace isn't found, it will return a not found code with an exception",
                responseCode = "404",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[WorkspaceException]))
                )
            ),
            new ApiResponse(
                description = "In case theres no valid JWT Token it will return an error",
                responseCode = "401",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[JsObject]))
                )
            ),
            new ApiResponse(
                description = "In case something unknown happens, it will return a 500 error",
                responseCode = "500",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[WorkspaceException]))
                )
            )
        )
    )
    def saveWorkspace: Action[MultipartFormData[Files.TemporaryFile]]

    @Path("user")
    @GET
    @Consumes(Array(MediaType.APPLICATION_JSON))
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = "Update your accounts profile",
        tags = Array("onboarding"),
        requestBody = new RequestBody(
            description = "Will receive the user-key of whom it wants to get the metadata",
            required = true,
            content = Array(
                new Content(schema = new Schema(implementation = classOf[String]))
            )
        ),
        responses = Array(
            new ApiResponse(
                description = "In case everything went well, will respond with its data",
                responseCode = "200",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[UserOnboardingResponse]))
                )
            ),
            new ApiResponse(
                description = "In case the user isn't found, it will return a not found code with an exception",
                responseCode = "404",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[AccountException]))
                )
            ),
            new ApiResponse(
                description = "In case theres no valid JWT Token it will return an error",
                responseCode = "401",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[JsObject]))
                )
            ),
            new ApiResponse(
                description = "In case something unknown happens, it will return a 500 error",
                responseCode = "500",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[AccountException]))
                )
            )
        )
    )
    def getUserMetadata(userKey: String): Action[AnyContent]
}
