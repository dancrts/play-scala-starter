package controllers.api.auth

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs.{Consumes, POST, Path, Produces}
import jakarta.ws.rs.core.MediaType
import play.api.mvc.{Action, AnyContent}
import services.account.dto.{AuthRequest, GoogleLoginRequest}

trait AuthController {

    @Path("login")
    @POST
    @Consumes(Array(MediaType.APPLICATION_JSON))
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = "Login user",
        tags = Array("Oauth"),
        requestBody = new RequestBody(
            required = true,
            content = Array(
                new Content(schema = new Schema(implementation = classOf[AuthRequest]))
            )
        ),
        responses = Array(
            new ApiResponse(
                responseCode = "200",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[String]))
                )
            ),
            new ApiResponse(
                responseCode = "401",
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[String])
                    )
                )
            ),
            new ApiResponse(
                responseCode = "404",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[String]))
                )
            )
        )
    )
    def login: Action[AnyContent]

    @Path("register")
    @POST
    @Consumes(Array(MediaType.APPLICATION_JSON))
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = "Register user",
        tags = Array("Oauth"),
        requestBody = new RequestBody(
            required = true,
            content = Array(
                new Content(schema = new Schema(implementation = classOf[AuthRequest]))
            )
        ),
        responses = Array(
            new ApiResponse(
                responseCode = "200",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[String]))
                )
            ),
            new ApiResponse(
                responseCode = "401",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[String]))
                )
            ),
            new ApiResponse(
                responseCode = "404",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[String]))
                )
            )
        )
    )
    def register: Action[AnyContent]

    @Path("google")
    @POST
    @Consumes(Array(MediaType.APPLICATION_JSON))
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = "Continue with Google",
        tags = Array("Oauth"),
        requestBody = new RequestBody(
            required = true,
            content = Array(
                new Content(schema = new Schema(implementation = classOf[GoogleLoginRequest]))
            )
        ),
        responses = Array(
            new ApiResponse(
                responseCode = "200",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[String]))
                )
            ),
            new ApiResponse(
                responseCode = "401",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[String]))
                )
            ),
            new ApiResponse(
                responseCode = "404",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[String]))
                )
            )
        )
    )
    def loginWithGoogle: Action[AnyContent]
}
