package com.cloud.urlshortenerservice;

import com.cloud.urlshortenerservice.controller.UrlController;
import com.cloud.urlshortenerservice.entity.Url;
import com.cloud.urlshortenerservice.model.TokenDto;
import com.cloud.urlshortenerservice.model.UrlRequestDto;
import com.cloud.urlshortenerservice.util.AppResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class UrlShortenerServiceApplicationTests {

	private static final String TOKEN_FOR_FREE_LOGGED_IN_USER = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZXJ5ZW0iLCJ1c2VySWQiOiI2MDc5YTY2ODgyNDVkMTM1OGU0Yjc0OTQiLCJhY2NvdW50VHlwZSI6ImZyZWUiLCJleHAiOjE2MTkxMTQ4OTN9.wkw4vq4YK7o3gn33rFpmLoeZ_541egI8ECNu6IMjAFXsDGt2UMUbyOrqt3SS57AKh3ycnq_9PY4VrGpIjRl7SQ";
	private static final String TOKEN_FOR_PREMIUM_LOGGED_IN_USER = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZXJ5ZW05NyIsInVzZXJJZCI6IjYwN2MxYWUyMGE3M2U1NTIzNDZiMWNlOCIsImFjY291bnRUeXBlIjoicHJlbWl1bSIsImV4cCI6MTYxOTExNDkxNn0.Pvnz91vxjK25OPFBtkXfJW9FiN6EbF6gbfbVPCbrScz5D48SX5ysx_O8GHpy-AiGLDpphGKx63aR4rCDXK5kIA";
	private static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZXJ5ZW0iLCJ1c2VySWQiOiI2MDc5YTY2ODgyNDVkMTM1OGU0Yjc0OTQiLCJhY2NvdW50VHlwZSI6ImZyZWUiLCJleHAiOjE2MTg1ODc5MDJ9.YNf8-1YwEio4isp1iSC_V2-MuL_EE9-44UaDW9sdoEngg5kZjuNc50IKPFgo88P8E8iyl5Shju0g81pWdG0h1A";
	private static final String VALID_URL = "https://www.google.com/";
	private static final String INVALID_URL = "httpsgooglecom";

	private final static String ERR_URL_NOT_VALID = "You have to enter a valid URL!";
	private static final String ERR_USER_AUTH = "The user is not logged in!";
	private final static String ERR_DUPLICATE_KEY = "This key already exists!";

	@Autowired
	UrlController urlController;

	@Test
	void testShortenEmptyUrlRequest(){

		UrlRequestDto urlRequest1 = new UrlRequestDto();
		testShortenInvalidUrlRequest(urlRequest1);
	}

	@Test
	void testShortenInvalidUrlRequestSuite(){

		UrlRequestDto urlRequest1 = new UrlRequestDto();
		urlRequest1.setOriginalUrl(INVALID_URL);
		testShortenInvalidUrlRequest(urlRequest1);

		UrlRequestDto urlRequest2 = new UrlRequestDto();
		urlRequest2.setOriginalUrl(INVALID_URL);
		urlRequest2.setShortKey("shortKey");
		testShortenInvalidUrlRequest(urlRequest2);

		UrlRequestDto urlRequest3 = new UrlRequestDto();
		urlRequest3.setOriginalUrl(INVALID_URL);
		urlRequest3.setShortKey("shortKey");
		urlRequest3.setToken("token");
		testShortenInvalidUrlRequest(urlRequest3);

	}

	void testShortenInvalidUrlRequest(UrlRequestDto urlRequestDto){

		AppResponse<Url> response = urlController.shortenURL(urlRequestDto);
		Assertions.assertEquals(false, response.getSuccessful());
		Assertions.assertEquals( ERR_URL_NOT_VALID, response.getErrorReason());
	}

	@Test
	void testShortenValidUrlRequest(){

		UrlRequestDto urlRequest = new UrlRequestDto();
		urlRequest.setOriginalUrl(VALID_URL);

		AppResponse<Url> response = urlController.shortenURL(urlRequest);
		Assertions.assertEquals(true, response.getSuccessful());
	}

	@Test
	void testShortenValidUrlRequestWithShortKey(){

		String shortKey = "myShortKey";

		UrlRequestDto urlRequest = new UrlRequestDto();
		urlRequest.setOriginalUrl(VALID_URL);
		urlRequest.setShortKey(shortKey);

		AppResponse<Url> response = urlController.shortenURL(urlRequest);
		Assertions.assertEquals(true, response.getSuccessful());
		Assertions.assertNotEquals(shortKey, response.getData().getShortenKey());

	}

	@Test
	void testShortenValidUrlForExpiredUser(){

		UrlRequestDto urlRequest = new UrlRequestDto();
		urlRequest.setOriginalUrl(VALID_URL);
		urlRequest.setShortKey("mynewshortkey");
		urlRequest.setToken(EXPIRED_TOKEN);

		AppResponse<Url> response = urlController.shortenURL(urlRequest);
		Assertions.assertEquals(false, response.getSuccessful());
		Assertions.assertEquals( ERR_USER_AUTH, response.getErrorReason());
	}

	@Test
	void testShortenValidUrlForLoggedInFreeUser(){
		UrlRequestDto urlRequest = new UrlRequestDto();
		urlRequest.setOriginalUrl(VALID_URL);
		urlRequest.setToken(TOKEN_FOR_FREE_LOGGED_IN_USER);

		AppResponse<Url> response = urlController.shortenURL(urlRequest);
		Assertions.assertEquals(true, response.getSuccessful());
		Assertions.assertNotEquals(null, response.getData().getExpirationDate());
	}

	@Test
	void testShortenValidUrlForLoggedInPremiumUser(){
		UrlRequestDto urlRequest = new UrlRequestDto();
		urlRequest.setOriginalUrl(VALID_URL);
		urlRequest.setToken(TOKEN_FOR_PREMIUM_LOGGED_IN_USER);

		AppResponse<Url> response = urlController.shortenURL(urlRequest);
		Assertions.assertEquals(true, response.getSuccessful());
		Assertions.assertNull(response.getData().getExpirationDate());
	}

	@Test
	void testShortenValidUrlForLoggedInFreeUserWithCustomKey(){
		UrlRequestDto urlRequest = new UrlRequestDto();
		urlRequest.setOriginalUrl(VALID_URL);
		urlRequest.setShortKey("free-key1");
		urlRequest.setToken(TOKEN_FOR_FREE_LOGGED_IN_USER);

		AppResponse<Url> response = urlController.shortenURL(urlRequest);
		Assertions.assertEquals(true, response.getSuccessful());
		Assertions.assertNotEquals(null, response.getData().getExpirationDate());
		Assertions.assertEquals("free-key1", response.getData().getShortenKey());
	}

	@Test
	void testShortenValidUrlForLoggedInPremiumUserWithCustomKey(){
		UrlRequestDto urlRequest = new UrlRequestDto();
		urlRequest.setOriginalUrl(VALID_URL);
		urlRequest.setShortKey("premium-key1");
		urlRequest.setToken(TOKEN_FOR_PREMIUM_LOGGED_IN_USER);

		AppResponse<Url> response = urlController.shortenURL(urlRequest);
		Assertions.assertEquals(true, response.getSuccessful());
		Assertions.assertNull(response.getData().getExpirationDate());
		Assertions.assertEquals("premium-key1", response.getData().getShortenKey());
	}

	@Test
	void testShortenValidUrlForLoggedInFreeUserWithDuplicateCustomKey(){

		// first request
		UrlRequestDto urlRequest = new UrlRequestDto();
		urlRequest.setOriginalUrl(VALID_URL);
		urlRequest.setToken(TOKEN_FOR_FREE_LOGGED_IN_USER);

		AppResponse<Url> response = urlController.shortenURL(urlRequest);
		String shortKey = response.getData().getShortenKey();

		// second request
		urlRequest = new UrlRequestDto();
		urlRequest.setOriginalUrl(VALID_URL);
		urlRequest.setShortKey(shortKey);
		urlRequest.setToken(TOKEN_FOR_FREE_LOGGED_IN_USER);

		response = urlController.shortenURL(urlRequest);

		Assertions.assertEquals(false, response.getSuccessful());
		Assertions.assertEquals(ERR_DUPLICATE_KEY, response.getErrorReason());

	}

	@Test
	void testShortenValidUrlForLoggedInPremiumUserWithDuplicateCustomKey(){

		// first request
		UrlRequestDto urlRequest = new UrlRequestDto();
		urlRequest.setOriginalUrl(VALID_URL);
		urlRequest.setToken(TOKEN_FOR_PREMIUM_LOGGED_IN_USER);

		AppResponse<Url> response = urlController.shortenURL(urlRequest);
		String shortKey = response.getData().getShortenKey();

		// second request
		urlRequest = new UrlRequestDto();
		urlRequest.setOriginalUrl(VALID_URL);
		urlRequest.setShortKey(shortKey);
		urlRequest.setToken(TOKEN_FOR_PREMIUM_LOGGED_IN_USER);

		response = urlController.shortenURL(urlRequest);

		Assertions.assertEquals(false, response.getSuccessful());
		Assertions.assertEquals(ERR_DUPLICATE_KEY, response.getErrorReason());
	}

	@Test
	void testDeleteUrlByFreeUser(){

		// first request
		UrlRequestDto urlRequest = new UrlRequestDto();
		urlRequest.setOriginalUrl(VALID_URL);
		urlRequest.setToken(TOKEN_FOR_FREE_LOGGED_IN_USER);

		AppResponse<Url> response = urlController.shortenURL(urlRequest);
		String shortKey = response.getData().getShortenKey();

		// delete request
		TokenDto tokenDto = new TokenDto(TOKEN_FOR_FREE_LOGGED_IN_USER);
		AppResponse<Optional<Url>> optionalResponse = urlController.deleteURL(shortKey, tokenDto);
		Assertions.assertNotEquals(null, optionalResponse.getData());
		Assertions.assertEquals(shortKey, optionalResponse.getData().get().getShortenKey());
	}

	@Test
	void testDeleteUrlByPremiumUser(){

		// first request
		UrlRequestDto urlRequest = new UrlRequestDto();
		urlRequest.setOriginalUrl(VALID_URL);
		urlRequest.setToken(TOKEN_FOR_PREMIUM_LOGGED_IN_USER);

		AppResponse<Url> response = urlController.shortenURL(urlRequest);
		String shortKey = response.getData().getShortenKey();

		// delete request
		TokenDto tokenDto = new TokenDto(TOKEN_FOR_PREMIUM_LOGGED_IN_USER);
		AppResponse<Optional<Url>> optionalResponse = urlController.deleteURL(shortKey, tokenDto);
		Assertions.assertNotEquals(null, optionalResponse.getData());
		Assertions.assertEquals(shortKey, optionalResponse.getData().get().getShortenKey());
	}

}