package exception;

/**
 * �ظ���ɱ�쳣(�������쳣)
 * RuntimeException ����Ҫtry/catch ����Spring ������ʽ����ֻ����RuntimeException�ع�����.
 * Created by wchb7 on 16-5-14.
 */
public class RepeatKillException extends SeckillException{
	public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
