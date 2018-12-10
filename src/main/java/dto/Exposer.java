package dto;

import java.io.Serializable;

//数据传输层DTO

//暴露秒杀地址DTO(dto:web层和service层传递数据用
//这些跟实际业务关系不大，供exportSeckillUrl方法使用
//前端可以跟据开始时间与结束时间就可以判断前端要显示的逻辑
public class Exposer  implements Serializable{
	@Override
	public String toString() {
		return "Exposer [exposed=" + exposed + ", md5=" + md5 + ", seckillId=" + seckillId + ", now=" + now + ", start="
				+ start + ", end=" + end + "]";
	}

	/**
     * 秒杀是否开启
     */
    private boolean exposed;

    private String md5;

    private long seckillId;
    
    /**
     * 系统时间(毫秒)，如果时间没到，就不能返回地址
     */
    private long now;

    private long start;//秒杀开启时间结束时间
    private long end;
    
    public Exposer(boolean exposed, String md5, long seckillId) {
		super();
		this.exposed = exposed;
		this.md5 = md5;
		this.seckillId = seckillId;
	}

    public Exposer(boolean exposed, long seckillId, long now, long start, long end) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }
	
	public Exposer(boolean exposed, long seckillId) {
		super();
		this.exposed = exposed;
		this.seckillId = seckillId;
	}

	public boolean isExposed() {
		return exposed;
	}

	public void setExposed(boolean exposed) {
		this.exposed = exposed;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}

	public long getNow() {
		return now;
	}

	public void setNow(long now) {
		this.now = now;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	

}
