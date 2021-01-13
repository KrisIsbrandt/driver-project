package pl.isbrandt.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

public class PaginatedResultsRetrievedEvent<T extends Serializable> extends ApplicationEvent {

    private final UriComponentsBuilder uriBuilder;
    private final HttpServletResponse response;
    private final String apiEndpointPrefix;
    private final int page;
    private final int totalPages;
    private final int size;

    public PaginatedResultsRetrievedEvent(Class<T> clazz,
                                          UriComponentsBuilder uriBuilder,
                                          HttpServletResponse response,
                                          String apiEndpointPrefix,
                                          int page,
                                          int totalPages,
                                          int size) {
        super(clazz);
        this.uriBuilder = uriBuilder;
        this.response = response;
        this.apiEndpointPrefix = apiEndpointPrefix;
        this.page = page;
        this.totalPages = totalPages;
        this.size = size;
    }

    public Class<T> getClazz() {
        return (Class<T>) getSource();
    }

    public UriComponentsBuilder getUriBuilder() {
        return uriBuilder;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getSize() {
        return size;
    }

    public String getApiEndpointPrefix() {
        return apiEndpointPrefix;
    }
}
