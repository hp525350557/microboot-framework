package org.microboot.core.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.utils.LoggerUtils;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author 胡鹏
 */
@Setter
@Getter
public class Page implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(Page.class);

    //总记录数
    private long total;
    //每页记录数
    private long pageSize;
    //当前页数
    private long pageNumber;
    //总页数
    private long totalPage;
    //开始行号(从0开始)
    private long offset;
    //开始行号(从1开始)
    private long startRow;
    //结束行号
    private long endRow;
    //列表数据
    private List<Map<String, Object>> rows;
    //非常规结构数据
    private Object data;

    static {
        try {
            /*
                需求：
                    使用Builder构建对象，并且禁用new关键字创建对象
                问题：
                    大多数时候，利用反射(类.class)创建该对象时，默认调用的是无参构造方法
                    所以，必须声明一个无参构造方法，但是又不想将无参构造方法的访问级别设置为public
                解决：
                    通过ReflectionUtils.makeAccessible(Constructor<?> ctor)
                    设置在反射时忽略无参构造方法的private修饰符
             */
            ReflectionUtils.makeAccessible(Page.class.getDeclaredConstructor());
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
        }
    }

    private Page() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        //Page对象
        private final Page page = new Page();

        private Builder() {
        }

        public Builder setTotal(long total) {
            this.page.total = NumberUtils.max(0, total);
            return this;
        }

        public Builder setPageSize(long pageSize) {
            this.page.pageSize = NumberUtils.max(0, pageSize);
            return this;
        }

        public Builder setPageNumber(long pageNumber) {
            this.page.pageNumber = NumberUtils.max(0, pageNumber);
            return this;
        }

        public Builder setTotalPage(long totalPage) {
            this.page.totalPage = NumberUtils.max(0, totalPage);
            return this;
        }

        public Builder setOffset(long offset) {
            this.page.offset = NumberUtils.max(0, offset);
            return this;
        }

        public Builder setStartRow(long startRow) {
            this.page.startRow = NumberUtils.max(0, startRow);
            return this;
        }

        public Builder setEndRow(long endRow) {
            this.page.endRow = NumberUtils.max(0, endRow);
            return this;
        }

        public Builder generate() {
            boolean b1 = this.page.total <= 0;
            boolean b2 = this.page.pageSize <= 0;
            boolean b3 = this.page.pageNumber <= 0;
            //计算总页数
            this.page.totalPage = (b1 || b2) ? 0 : (this.page.total - 1) / this.page.pageSize + 1;
            //计算开始行号(从0开始)
            this.page.offset = (b2 || b3) ? 0 : (this.page.pageNumber - 1) * this.page.pageSize;
            //计算开始行号(从1开始)
            this.page.startRow = (b2 || b3) ? 0 : (this.page.pageNumber - 1) * this.page.pageSize + 1;
            //计算结束行号
            this.page.endRow = (b2 || b3) ? 0 : this.page.pageNumber * this.page.pageSize;
            return this;
        }

        public Page create() {
            return this.page;
        }
    }
}
