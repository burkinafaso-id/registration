package io.mosip.authentication.spin.service.impl.validator;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.id.validator.IdAuthValidator;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.pinvalidator.exception.InvalidPinException;
import io.mosip.kernel.pinvalidator.impl.PinValidatorImpl;

/**
 * 
 * This Class Provides the validation for StaticPinRequestValidator class.
 * 
 * @author Prem Kumar
 *
 */
@Component
public class StaticPinRequestValidator extends IdAuthValidator {

	/** The Constant UIN_VID. */
	private static final String UIN_VID = "UIN/VID";

	/** The Constant IDV_ID_TYPE. */
	private static final String IDV_ID_TYPE = "individualIdType";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";

	/** The Constant MISSING_INPUT_PARAMETER. */
	private static final int MISSING_INPUT_PARAMETER = 0;

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/** The Constant STATIC PIN. */
	private static final String STATIC_PIN = "staticPin";

	/** The Constant INDIVIDUAL_ID. */
	private static final String INDIVIDUAL_ID = "individualId";

	/** The Constant REQ_TIME. */
	private static final String REQ_TIME = "requestTime";

	private static final Object PIN = "PIN";

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(StaticPinRequestValidator.class);

	@Autowired
	PinValidatorImpl pinvalidator;

	@Override
	public boolean supports(Class<?> clazz) {
		return StaticPinRequestDTO.class.equals(clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.spin.validator.
	 * StaticPinRequestValidator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {

		if (Objects.nonNull(target)) {
			StaticPinRequestDTO staticPinRequestDTO = (StaticPinRequestDTO) target;
			validateId(staticPinRequestDTO.getId(), errors);
			validateReqTime(staticPinRequestDTO.getRequestTime(), errors, REQ_TIME);
			validateUinVidValue(staticPinRequestDTO, errors);
			validateStaticPin(staticPinRequestDTO.getRequest().getStaticPin(), errors);
		}
	}

	/**
	 * Validation for static Pin Null or empty check.
	 * 
	 * @param pinValue
	 * @param errors
	 */
	private void validateStaticPin(String pinValue, Errors errors) {
		if (Objects.isNull(pinValue) || pinValue.isEmpty()) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "validateStaticPin",
					MISSING_INPUT_PARAMETER + STATIC_PIN);
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorCode(),
					new Object[] { PIN }, IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorMessage());
		} else {
			try {
				pinvalidator.validatePin(pinValue);
			} catch (InvalidPinException e) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "validateStaticPin",
						"INVALID_INPUT_PARAMETER - pinValue - value -> " + pinValue);
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { STATIC_PIN },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}
		}

	}

	/**
	 * Validation for Uin and vid.
	 * 
	 * @param staticPinRequestDTO
	 * @param errors
	 */
	private void validateUinVidValue(StaticPinRequestDTO staticPinRequestDTO, Errors errors) {
		String idType = staticPinRequestDTO.getIndividualIdType();
		String value = staticPinRequestDTO.getIndividualId();
		if (idType.equals(IdType.UIN.getType())) {
			validateIdvId(value, IdType.UIN.getType(), errors, INDIVIDUAL_ID);
		} else if (idType.equals(IdType.VID.getType())) {
			validateIdvId(value, IdType.VID.getType(), errors, INDIVIDUAL_ID);
		} else {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "validateUinVidValue",
					MISSING_INPUT_PARAMETER + UIN_VID);
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IDV_ID_TYPE },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}
}
