package com.school.store.base.repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.school.store.base.model.SqlParams;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface IBaseRepository<T,ID extends Serializable> extends PagingAndSortingRepository<T, ID> {
	  /**
    * 方法名:delete</p> 
    * 描述: 删除对象</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:24:07 </p>
    * 参数: @param o </p>   
    * 返回类型: void </p>
     */
    public void delete(T o);
    /**
    * 方法名:update</p> 
    * 描述:更新对象</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:29:57 </p>
    * 参数: @param o </p>   
    * 返回类型: void </p>
     */
    public void update(T o);
    /**
     * 
    * 方法名:saveOrUpdate</p> 
    * 描述: 保存或更新一个对象</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:30:29 </p>
    * 参数: @param o </p>   
    * 返回类型: void </p>
     */
     public void saveOrUpdate(T o);
    /**
     * 
    * 方法名:get</p> 
    * 描述: 通过主键获得对象</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:30:53 </p>
    * 参数: @param c
    * 参数: @param id
    * 参数: @return </p>   
    * 返回类型: T </p>
     */
    public T get(Class<T> c, Serializable id);

    /**
     * 
    * 方法名:get</p> 
    * 描述: 通过sqString语句获取1个对象</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:31:26 </p>
    * 参数: @param sqString sqString语句
    * 参数: @return </p>   
    * 返回类型: T </p>
     */
    public T get(String sqString);

    /**
     * 
    * 方法名:find</p> 
    * 描述: 获取查询对象列表</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:32:52 </p>
    * 参数: @param sqString
    * 参数: @return </p>   
    * 返回类型: List<T> </p>
     */
    public List<T> find(String sqString);

    /**
     * 
    * 方法名:find</p> 
    * 描述: 分页查询</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:33:26 </p>
    * 参数: @param sqString
    * 参数: @param page 页码
    * 参数: @param rows 行数(1页显示多少行)
    * 参数: @return </p>   
    * 返回类型: List<T> </p>
     */
    public List<T> find(String sqString, int page, int rows);

    /**
     * 
    * 方法名:find</p> 
    * 描述: 分页查询</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:34:18 </p>
    * 参数: @param sqString
    * 参数: @param params
    * 参数: @param page
    * 参数: @param rows
    * 参数: @return </p>   
    * 返回类型: List<T> </p>
     */
    public List<T> find(String sqString, Map<String, Object> params, int page,
                        int rows);

    /**
     * 
    * 方法名:count</p> 
    * 描述: 统计数目</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:34:28 </p>
    * 参数: @param sqString
    * 参数: @return </p>   
    * 返回类型: Long </p>
     */
    public Long count(String sqString);

    /**
     * 
    * 方法名:count</p> 
    * 描述: 统计数目</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:34:48 </p>
    * 参数: @param sqString
    * 参数: @param params
    * 参数: @return </p>   
    * 返回类型: Long </p>
     */
    public Long count(String sqString, Map<String, Object> params);

    /**
     * 
    * 方法名:executesqString</p> 
    * 描述: 执行了几条sql语句</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:35:09 </p>
    * 参数: @param sqString
    * 参数: @return 执行响应数目</p>   
    * 返回类型: int </p>
     */
    public int executesqString(String sqString);

    /**
     * 
    * 方法名:executesqString</p> 
    * 描述: sqString语句响应数目</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:35:56 </p>
    * 参数: @param sqString
    * 参数: @param params
    * 参数: @return 执行响应数目</p>   
    * 返回类型: int </p>
     */
    public int executesqString(String sqString, Map<String, Object> params);


    /**
     * 
    * 方法名:findBySql</p> 
    * 描述: 获取sql查询结果</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:37:47 </p>
    * 参数: @param sql sql语句
    * 参数: @param params 参数
    * 参数: @return </p>   
    * 返回类型: List<Object[]> </p>
     */
    public List<Object[]> findBySql(String sql, Map<String, Object> params);

    /**
     * 
    * 方法名:findBySql</p> 
    * 描述: 获取sql分页查询结果</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:39:22 </p>
    * 参数: @param sql
    * 参数: @param params
    * 参数: @param page
    * 参数: @param rows
    * 参数: @return </p>   
    * 返回类型: List<Object[]> </p>
     */
    public List<Object[]> findBySql(String sql, Map<String, Object> params,
                                    int page, int rows);

    /**
     * 
    * 方法名:executeSql</p> 
    * 描述: sql语句响应数目</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:39:39 </p>
    * 参数: @param sql
    * 参数: @return 语句响应数目</p>   
    * 返回类型: int </p>
     */
    public int executeSql(String sql);

    /**
     * 
    * 方法名:executeSql</p> 
    * 描述: sql语句响应数目</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:40:35 </p>
    * 参数: @param sql
    * 参数: @param params
    * 参数: @return </p>   
    * 返回类型: int </p>
     */
    public int executeSql(String sql, Map<String, Object> params);

    /**
     * 
    * 方法名:countBySql</p> 
    * 描述: 统计数目</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:40:44 </p>
    * 参数: @param sql
    * 参数: @return </p>   
    * 返回类型: BigInteger </p>
     */
    public BigInteger countBySql(String sql);

    /**
     * 
    * 方法名:countBySql</p> 
    * 描述:统计数目</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:41:01 </p>
    * 参数: @param sql
    * 参数: @param params
    * 参数: @return </p>   
    * 返回类型: BigInteger </p>
     */
    public BigInteger countBySql(String sql, Map<String, Object> params);

    /**
     * 
    * 方法名:batchSave</p> 
    * 描述: 批量添加保存对象</p>
    * 作者:Dr·子烈  </p>
    * 日期: 2017年12月20日 下午9:41:13 </p>
    * 参数: @param entitys
    * 参数: @throws SQLException </p>   
    * 返回类型: void </p>
     */
    public void batchSave(List<T> entitys) throws SQLException;



    public List<T> findByDynamicSqlParams(String tableName, SqlParams sqlParams, int page, int rows, Class clazz);
}
