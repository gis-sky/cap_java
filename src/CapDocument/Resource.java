package CapDocument;

public class Resource {
    public String resourceDesc;
    public String mimeType;
    public int size;
    public String uri;
    public String derefUri;
    public String digest;

    public Resource(String resourceDesc)
    {
        this.resourceDesc = resourceDesc;
    }
}
