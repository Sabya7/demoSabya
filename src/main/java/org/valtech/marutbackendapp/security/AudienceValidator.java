package org.valtech.marutbackendapp.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;

import java.util.List;

public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private String audience;

    public AudienceValidator(String audience) {
        Assert.hasText(audience,"Please send a valid audience.");
        this.audience=audience;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {

        List<String> audiences = jwt.getAudience();

        if (audiences.contains(audience))
            return OAuth2TokenValidatorResult.success();
        else
            return OAuth2TokenValidatorResult.failure(new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN));

    }
}
