package controllers.api.auth

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs.{Consumes, POST, Path, Produces}
import jakarta.ws.rs.core.MediaType
import play.api.mvc.{Action, AnyContent}
import com.qrsof.jwt.models.JwtToken
import services.account.AccountException
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
            description = "It will receive credentials",
            required = true,
            content = Array(
                new Content(schema = new Schema(implementation = classOf[AuthRequest]))
            )
        ),
        responses = Array(
            new ApiResponse(
                description = "In case credentials matched and the account exists, will grant access via a JWT token",
                responseCode = "200",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[JwtToken]))
                )
            ),
            new ApiResponse(
                description = "In case something unexpected goes wrong with the APPTack, it will return a BadRequest code",
                responseCode = "400",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[AccountException]))
                )
            ),
            new ApiResponse(
                description = "In case credentials do not match, it will return an Unauthorized code ",
                responseCode = "401",
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[AccountException])
                    )
                )
            ),
            new ApiResponse(
                description = "In case it does not find an account, it will return a NotFound code",
                responseCode = "404",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[AccountException]))
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
    def login: Action[AnyContent]

    @Path("register")
    @POST
    @Consumes(Array(MediaType.APPLICATION_JSON))
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = "Register user",
        tags = Array("Oauth"),
        requestBody = new RequestBody(
            description = "Will receive some credentials to create an account",
            required = true,
            content = Array(
                new Content(schema = new Schema(implementation = classOf[AuthRequest]))
            )
        ),
        responses = Array(
            new ApiResponse(
                description = "In case those credentials do not exist and they're properly saved, it will grant access via a JWT token",
                responseCode = "200",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[JwtToken]))
                )
            ),
            new ApiResponse(
                description = "In case something unexpected goes wrong with the APPTack, it will return a BadRequest code",
                responseCode = "400",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[AccountException]))
                )
            ),
            new ApiResponse(
                description = "In case those credentials already exist, they will return a Conflict code",
                responseCode = "409",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[AccountException]))
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
    def register: Action[AnyContent]

    @Path("google")
    @POST
    @Consumes(Array(MediaType.APPLICATION_JSON))
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = "Continue with Google",
        tags = Array("Oauth"),
        requestBody = new RequestBody(
            description = "",
            required = true,
            content = Array(
                new Content(schema = new Schema(implementation = classOf[GoogleLoginRequest]))
            )
        ),
        responses = Array(
            new ApiResponse(
                description = "Whenever it successfully logins with google and the AppTack it will return a JWT Token",
                responseCode = "200",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[JwtToken]))
                )
            ),
            new ApiResponse(
                description = "In case something unexpected goes wrong with either the APPTack or the GoogleToken, it will return a BadRequest code",
                responseCode = "400",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[AccountException]))
                )
            ),
            new ApiResponse(
                description = "In case that account doesnt have a permission, it will return an Unauthorized code",
                responseCode = "401",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[AccountException]))
                )
            ),
            new ApiResponse(
                description = "In case the AppTack does not find the resource or the account, it will return a NotFound code",
                responseCode = "404",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[AccountException]))
                )
            ),
            new ApiResponse(
                description = "In case something causes an unexpected error, it will return an InternalServerError code",
                responseCode = "500",
                content = Array(
                    new Content(schema = new Schema(implementation = classOf[AccountException]))
                )
            )
        )
    )
    def loginWithGoogle: Action[AnyContent]
}
