package com.link.utils;

import com.link.service.LinkService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author : wangaidong
 * @date : 2023/11/28 16:02
 */
@Component
public class ShortLinkUtil {

	@Lazy
	@Resource
	private LinkService linkService;

	private static final char[] SHORT_LINK_CHAR_LIBRARY = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

	private static final Integer LIBRARY_LENGTH;

	private static final int SHORT_LEN = 6;

	private static final int RETRY_NUMBER = 1000;

	static {
		LIBRARY_LENGTH = SHORT_LINK_CHAR_LIBRARY.length;
	}

	public String generateShortSuffixAndRetry() {
		for (int i = 0; i < RETRY_NUMBER; i++) {
			String shortSuffix = generateShortSuffix();
			if (StringUtils.isBlank(linkService.getShortSuffixByLongLink(shortSuffix))) {
				return shortSuffix;
			}
		}

		return null;
	}

	public String generateShortSuffix() {
		StringBuilder builder = new StringBuilder(LIBRARY_LENGTH);
		for (int i = 0; i < SHORT_LEN; i++) {
			builder.append(SHORT_LINK_CHAR_LIBRARY[ThreadLocalRandom.current().nextInt(LIBRARY_LENGTH)]);
		}

		return builder.toString();
	}

}
