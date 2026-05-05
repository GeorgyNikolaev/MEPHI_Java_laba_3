package parser;

import domain.Mission;
import exception.MissionParsingException;

import java.util.Map;

abstract class TextFormatParser extends MissionParser{
    abstract boolean supports(String content);
}
