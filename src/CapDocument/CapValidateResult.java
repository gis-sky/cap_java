package CapDocument;

public class CapValidateResult {
    private String _subject;
    private String _message;

    public CapValidateResult(String subject, String message)
    {
        _subject = subject;
        _message = message;
    }

    /**
     * 取得主旨
     * @return 主旨
     */
    public String GetSubject()
    {
        return _subject;
    }

    /**
     * 取得訊息
     * @return 訊息
     */
    public String GetMessage()
    {
        return _message;
    }
}
