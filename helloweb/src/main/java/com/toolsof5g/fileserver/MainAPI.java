package com.toolsof5g.fileserver;

import com.toolsof5g.interfaces.TokenAuth;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

@Path("/api")
public class MainAPI {
    @TokenAuth
    @GET
    @Path("/{filename}")
    // MediaType.APPLICATION_OCTET_STREAM
    @Produces(MediaType.TEXT_PLAIN)
    public synchronized Response getAll(@PathParam("filename") String fileName, @HeaderParam("authorization") String authString)
            throws Exception {

        // if(!APIUtils.isUserAuthenticated(authString)){
        //     return Response
        //             .status(Response.Status.UNAUTHORIZED)
        //             .header("WWW-Authenticate","Basic realm=\"Realm\"")
        //             .build();
        // }
        // else{
            String fileTempDirectory = APIUtils.getFileTempDirectory();
            if(!APIUtils.copyFile2TempDirectory(fileName, fileTempDirectory)){
                return Response
                        .status(Response.Status.FORBIDDEN)
                        .entity(APIUtils.getFilePathMap().toString())
                        .build();
            }

            File file = APIUtils.getFileFromTempDirectory(fileTempDirectory, fileName);
            if(!file.exists()){
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(file.toString())
                        .build();
            }

            String uRLFileName = APIUtils.decodeFileName2URL(file.getName());
            return Response
                    .ok(file)
                    .header("Content-disposition","attachment;filename=" +uRLFileName)
                    .header("Cache-Control", "no-cache")
                    .build();
        // }
    }
}

