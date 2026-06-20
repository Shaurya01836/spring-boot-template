package io.github.shaurya01836.template.exception;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${server.error.path:/error}")
public class GlobalErrorController implements ErrorController {

	@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<String> htmlError(HttpServletRequest request) {
		int status = resolveStatus(request);
		String path = resolvePath(request);
		String message = resolveMessage(request, status, path);

		return ResponseEntity.status(status)
			.contentType(MediaType.TEXT_HTML)
			.body(buildHtmlErrorPage(status, path, message));
	}

	@RequestMapping
	public ResponseEntity<Map<String, Object>> apiError(HttpServletRequest request) {
		int status = resolveStatus(request);
		String path = resolvePath(request);
		String message = resolveMessage(request, status, path);

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", OffsetDateTime.now().toString());
		body.put("status", status);
		body.put("error", resolveReason(status));
		body.put("message", message);
		body.put("path", path);

		return ResponseEntity.status(status)
			.contentType(MediaType.APPLICATION_JSON)
			.body(body);
	}

	private int resolveStatus(HttpServletRequest request) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		if (status instanceof Integer integer) {
			return integer;
		}
		if (status instanceof String stringStatus) {
			try {
				return Integer.parseInt(stringStatus);
			} catch (NumberFormatException ignored) {
				return HttpStatus.INTERNAL_SERVER_ERROR.value();
			}
		}
		return HttpStatus.INTERNAL_SERVER_ERROR.value();
	}

	private String resolvePath(HttpServletRequest request) {
		Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
		if (path instanceof String stringPath && !stringPath.isBlank()) {
			return stringPath;
		}
		return request.getRequestURI();
	}

	private String resolveMessage(HttpServletRequest request, int status, String path) {
		Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
		if (message instanceof String stringMessage && !stringMessage.isBlank()) {
			return stringMessage;
		}

		if (status == HttpStatus.NOT_FOUND.value() && path.startsWith("/v1/api/")) {
			return "No API endpoint is mapped for this request.";
		}

		return resolveReason(status);
	}

	private String resolveReason(int status) {
		HttpStatus httpStatus = HttpStatus.resolve(status);
		return httpStatus != null ? httpStatus.getReasonPhrase() : "Error";
	}

	private String buildHtmlErrorPage(int status, String path, String message) {
		String safeStatus = escapeHtml(Integer.toString(status));
		String safePath = escapeHtml(path);
		String safeMessage = escapeHtml(message);

		return """
			<!DOCTYPE html>
			<html lang="en">
			<head>
			  <meta charset="UTF-8" />
			  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
			  <title>Error __STATUS__</title>
			  <style>
			    * {
			      box-sizing: border-box;
			    }
			    body {
			      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
			      background-color: #f8fafc;
			      color: #0f172a;
			      display: flex;
			      align-items: center;
			      justify-content: center;
			      min-height: 100vh;
			      margin: 0;
			      padding: 24px;
			    }
			    .error-card {
			      background: #ffffff;
			      border: 1px solid #e2e8f0;
			      border-radius: 8px;
			      padding: 32px;
			      max-width: 480px;
			      width: 100%;
			      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
			    }
			    h1 {
			      font-size: 1.5rem;
			      margin: 0 0 12px;
			      font-weight: 700;
			      color: #0f172a;
			    }
			    p {
			      font-size: 0.925rem;
			      color: #475569;
			      margin: 0 0 20px;
			      line-height: 1.5;
			    }
			    .meta {
			      font-family: monospace;
			      font-size: 0.8rem;
			      background: #f1f5f9;
			      padding: 10px 12px;
			      border-radius: 4px;
			      color: #475569;
			      word-break: break-all;
			    }
			  </style>
			</head>
			<body>
			  <div class="error-card">
			    <h1>Error __STATUS__</h1>
			    <p>__MESSAGE__</p>
			    <div class="meta">Path: __PATH__</div>
			  </div>
			</body>
			</html>
			"""
				.replace("__STATUS__", safeStatus)
				.replace("__MESSAGE__", safeMessage)
				.replace("__PATH__", safePath);
	}

	private String escapeHtml(String value) {
		StringBuilder builder = new StringBuilder(value.length());
		for (char character : value.toCharArray()) {
			switch (character) {
				case '&' -> builder.append("&amp;");
				case '<' -> builder.append("&lt;");
				case '>' -> builder.append("&gt;");
				case '"' -> builder.append("&quot;");
				case '\'' -> builder.append("&#39;");
				default -> builder.append(character);
			}
		}
		return builder.toString();
	}
}