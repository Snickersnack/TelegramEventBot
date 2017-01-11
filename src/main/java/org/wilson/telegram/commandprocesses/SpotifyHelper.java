package org.wilson.telegram.commandprocesses;

import java.io.IOException;

import org.scribe.model.Token;
import org.wilson.telegram.config.BotConfig;

import com.github.imgur.ImgUr;
import com.github.imgur.api.upload.UploadRequest;
import com.github.imgur.api.upload.UploadRequest.Builder;
import com.github.imgur.api.upload.UploadResponse;

public class SpotifyHelper {

	public void upload() throws IOException{
		ImgUr imgurProvider = new ImgUr("YOUR ANONYMOUS API KEY");
		byte[] newimage = null;
		
		Token newToken = new Token(BotConfig.SPOTIFYTOKEN, BotConfig.SPOTIFYSECRET);
		
		Builder test = new UploadRequest.Builder().withImageData(newimage).withAccessToken(newToken);
		UploadRequest upload = test.build();
		UploadResponse response = imgurProvider.call(upload);
		response.getLinks().getImgurPage();
		
	}
}
