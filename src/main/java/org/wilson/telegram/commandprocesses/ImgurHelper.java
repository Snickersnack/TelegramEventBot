package org.wilson.telegram.commandprocesses;

import java.io.IOException;

import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.wilson.telegram.config.BotConfig;

import com.github.imgur.ImgUr;
import com.github.imgur.api.upload.UploadRequest;
import com.github.imgur.api.upload.UploadRequest.Builder;
import com.github.imgur.api.upload.UploadResponse;

public class ImgurHelper {

	public static String upload(byte[] newimage) throws IOException{
		ImgUr imgurProvider = new ImgUr(BotConfig.IMGURCLIENTID, BotConfig.IMGURSECRET);

		Token requestToken = imgurProvider.getRequestToken();
		String url = imgurProvider.getAuthorizationUrl(requestToken); // redirect your user on this url

		Verifier verifier = new Verifier(BotConfig.IMGURCLIENTID); // code given by imgur
		Token accessToken = imgurProvider.getAccessToken(requestToken, verifier); 
		
		Builder test = new UploadRequest.Builder().withImageData(newimage).withAccessToken(accessToken);
		UploadRequest upload = test.build();
		UploadResponse response = imgurProvider.call(upload);
		return response.getLinks().getImgurPage();
		
	}
}
