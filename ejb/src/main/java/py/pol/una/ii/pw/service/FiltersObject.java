package py.pol.una.ii.pw.service;

import com.google.gson.annotations.Expose;
import java.util.Map;
import org.primefaces.model.SortOrder;

public class FiltersObject {

    @Expose
    private int first;

    @Expose
    private int pageSize;

    @Expose
    private String sortField;

    @Expose
    private String sortOrder;

    @Expose
    private Map<String, Object> filters;
    


    public FiltersObject() {
    }

    public FiltersObject(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        this.first = first;
        this.pageSize = pageSize;
        
        if ( sortField == null ){
            this.sortField = "id";
        } else {
            this.sortField = sortField;
        }
        
        if (sortOrder.toString().contains("AS")) {
            this.sortOrder = "ASC";
        } else {
            this.sortOrder = "DESC";
        }
        
        this.filters = filters;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        if (sortOrder.toString().contains("AS")) {
            this.sortOrder = "ASC";
        } else {
            this.sortOrder = "DESC";
        }
    }

    public Map<String, Object> getFilters() {
        return this.filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }
}