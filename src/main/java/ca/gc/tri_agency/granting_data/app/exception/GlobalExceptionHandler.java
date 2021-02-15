package ca.gc.tri_agency.granting_data.app.exception;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DataRetrievalFailureException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public ModelAndView handleDataRetrievalFailureException(HttpServletRequest request, DataRetrievalFailureException drfe) {
		String exceptionType = drfe.getClass().getName();
		ModelAndView mv = new ModelAndView("/error");

		mv.addObject("exception", exceptionType.substring(exceptionType.lastIndexOf('.') + 1));
		mv.addObject("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
		mv.addObject("path", request.getRequestURL());
		mv.addObject("error", HttpStatus.NOT_FOUND.getReasonPhrase());
		mv.addObject("status", HttpStatus.NOT_FOUND.value());
		mv.addObject("message", drfe.getMessage());
		
		return mv;
	}

	@ExceptionHandler(FileNotFoundException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public ModelAndView handleFileNotFoundException(HttpServletRequest request, FileNotFoundException fne) {
		String exceptionType = fne.getClass().getName();
		ModelAndView mv = new ModelAndView("/error");

		mv.addObject("exception", exceptionType.substring(exceptionType.lastIndexOf('.') + 1));
		mv.addObject("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
		mv.addObject("path", request.getRequestURL());
		mv.addObject("error", HttpStatus.NOT_FOUND.getReasonPhrase());
		mv.addObject("status", HttpStatus.NOT_FOUND.value());
		mv.addObject("message", fne.getMessage());
		
		return mv;
	}

}
