/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.session.defaults;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.result.DefaultMapResultHandler;
import org.apache.ibatis.executor.result.DefaultResultContext;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

/**
 * 默认SqlSession实现
 * The default implementation for {@link SqlSession}.
 * Note that this class is not Thread-Safe.
 *
 * @author Clinton Begin
 */
public class DefaultSqlSession implements SqlSession {

  private final Configuration configuration;
  private final Executor executor;

  //是否自动提交
  private final boolean autoCommit;
  private boolean dirty;
  private List<Cursor<?>> cursorList;

  public DefaultSqlSession(Configuration configuration, Executor executor, boolean autoCommit) {
    this.configuration = configuration;
    this.executor = executor;
    this.dirty = false;
    this.autoCommit = autoCommit;
  }

  //默认非自动提交
  public DefaultSqlSession(Configuration configuration, Executor executor) {
    this(configuration, executor, false);
  }

//selectOne方法  2个
  @Override
  public <T> T selectOne(String statement) {
    return this.selectOne(statement, null);
  }

  //核心selectOne，上面的selectOne方法也转而调用该方法
  @Override
  public <T> T selectOne(String statement, Object parameter) {
    //如果得到0条则返回null，如果得到多条则报TooManyResultsException
    // Popular vote was to return null on 0 results and throw exception on too many.
    //转而调用selectList
    List<T> list = this.selectList(statement, parameter);
    if (list.size() == 1) {
      return list.get(0);
    } else if (list.size() > 1) {
      throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
    } else {
      //注意：当没有查询到结果的时候就会返回null。一般建议在mapper中编写resultType的时候使用包装类型，而不是基本类型，比如推荐使用Integer而不是int。这样就可以避免NPE
      return null;
    }
  }

//selectMap方法  3个
  @Override
  public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
    return this.selectMap(statement, null, mapKey, RowBounds.DEFAULT);
  }

  @Override
  public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
    return this.selectMap(statement, parameter, mapKey, RowBounds.DEFAULT);
  }

  //核心selectMap实现
  @Override
  public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
    //转而去调用selectList
    final List<? extends V> list = selectList(statement, parameter, rowBounds);
    final DefaultMapResultHandler<K, V> mapResultHandler = new DefaultMapResultHandler<>(mapKey,
            configuration.getObjectFactory(), configuration.getObjectWrapperFactory(), configuration.getReflectorFactory());
    final DefaultResultContext<V> context = new DefaultResultContext<>();
    //循环用DefaultMapResultHandler处理每条记录
    for (V o : list) {
      context.nextResultObject(o);
      mapResultHandler.handleResult(context);
    }
    //这个DefaultMapResultHandler里面存了所有已处理的记录(内部实现可能就是一个Map)，最后再返回一个Map
    return mapResultHandler.getMappedResults();
  }

//selectCursor方法  3个
//作用：用于查询百万级的数据，使用游标可以节省内存的消耗，不需要一次性取出所有数据，可以进行逐条处理或逐条取出部分批量处理。
  @Override
  public <T> Cursor<T> selectCursor(String statement) {
    return selectCursor(statement, null);
  }

  @Override
  public <T> Cursor<T> selectCursor(String statement, Object parameter) {
    return selectCursor(statement, parameter, RowBounds.DEFAULT);
  }

  //核心selectCursor实现
  @Override
  public <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds) {
    try {
      //根据statement找到对应的MappedStatement
      MappedStatement ms = configuration.getMappedStatement(statement);
      //转而用执行器来查询结果
      Cursor<T> cursor = executor.queryCursor(ms, wrapCollection(parameter), rowBounds);
      //累计存储Cursor
      registerCursor(cursor);
      return cursor;
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }

//selectList方法 作为selectOne和selectMap的实现   3个
  @Override
  public <E> List<E> selectList(String statement) {
    return this.selectList(statement, null);
  }

  @Override
  public <E> List<E> selectList(String statement, Object parameter) {
    return this.selectList(statement, parameter, RowBounds.DEFAULT);
  }

  //核心selectList实现
  @Override
  public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
    try {
      //根据statement找到对应的MappedStatement
      MappedStatement ms = configuration.getMappedStatement(statement);
      //转而用执行器来查询结果,注意这里传入的ResultHandler是null
      return executor.query(ms, wrapCollection(parameter), rowBounds, Executor.NO_RESULT_HANDLER);
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }

  @Override
  public void select(String statement, Object parameter, ResultHandler handler) {
    select(statement, parameter, RowBounds.DEFAULT, handler);
  }

  @Override
  public void select(String statement, ResultHandler handler) {
    select(statement, null, RowBounds.DEFAULT, handler);
  }

  ////核心select,带有ResultHandler，和selectList代码区别在于ResultHandler
  @Override
  public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
    try {
      MappedStatement ms = configuration.getMappedStatement(statement);
      //执行器来执行query
      executor.query(ms, wrapCollection(parameter), rowBounds, handler);
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }

//insert方法     2个
  @Override
  public int insert(String statement) {
    return insert(statement, null);
  }

  //核心insert方法
  @Override
  public int insert(String statement, Object parameter) {
    //insert是调用update
    return update(statement, parameter);
  }

//update方法    2个
  @Override
  public int update(String statement) {
    return update(statement, null);
  }

  //核心update方法
  @Override
  public int update(String statement, Object parameter) {
    try {
      //更新之前，dirty标志设置为true
      dirty = true;
      MappedStatement ms = configuration.getMappedStatement(statement);
      //执行器来执行update
      return executor.update(ms, wrapCollection(parameter));
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error updating database.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }

//delete方法   2个
  @Override
  public int delete(String statement) {
    //delete也是调用update
    return update(statement, null);
  }

  @Override
  public int delete(String statement, Object parameter) {
    //delete也是调用update
    return update(statement, parameter);
  }

//commit方法   2个
  @Override
  public void commit() {
    commit(false);
  }

  //核心commit方法
  @Override
  public void commit(boolean force) {
    try {
      //执行器来执行commit
      executor.commit(isCommitOrRollbackRequired(force));
      //每次commit之后，dirty标志设置为false
      dirty = false;
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error committing transaction.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }

//rollback方法   2个
  @Override
  public void rollback() {
    rollback(false);
  }

  //核心rollback
  @Override
  public void rollback(boolean force) {
    try {
      //使用执行器来执行rollback
      executor.rollback(isCommitOrRollbackRequired(force));
      //每次执行rollback之后，dirty标志设置为false
      dirty = false;
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error rolling back transaction.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }

  //核心flushStatements
  @Override
  public List<BatchResult> flushStatements() {
    try {
      //使用执行器执行flushStatements
      return executor.flushStatements();
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error flushing statements.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }

  //核心close
  @Override
  public void close() {
    try {
      //使用执行器执行close
      executor.close(isCommitOrRollbackRequired(false));
      //关闭游标
      closeCursors();
      //每次close之后，dirty标志设置为false
      dirty = false;
    } finally {
      ErrorContext.instance().reset();
    }
  }

  //核心closeCursors
  private void closeCursors() {
    if (cursorList != null && cursorList.size() != 0) {
      //遍历cursorList，挨个cursor关闭
      for (Cursor<?> cursor : cursorList) {
        try {
          cursor.close();
        } catch (IOException e) {
          throw ExceptionFactory.wrapException("Error closing cursor.  Cause: " + e, e);
        }
      }
      //最后清空cursorList
      cursorList.clear();
    }
  }

  @Override
  public Configuration getConfiguration() {
    return configuration;
  }

  @Override
  public <T> T getMapper(Class<T> type) {
    //调用mapperRegistry.getMapper
    return configuration.getMapper(type, this);
  }

  @Override
  public Connection getConnection() {
    try {
      //获取执行器事务中的连接
      return executor.getTransaction().getConnection();
    } catch (SQLException e) {
      throw ExceptionFactory.wrapException("Error getting a new connection.  Cause: " + e, e);
    }
  }

  //核心clearCache
  @Override
  public void clearCache() {
    //使用执行器执行clearLocalCache    清空localCache和localOutputParameterCache
    executor.clearLocalCache();
  }

  //累计增加cursor，通过ArrayList结构存储
  private <T> void registerCursor(Cursor<T> cursor) {
    if (cursorList == null) {
      cursorList = new ArrayList<>();
    }
    cursorList.add(cursor);
  }

  //检查是否需要强制commit或rollback
  private boolean isCommitOrRollbackRequired(boolean force) {
    return (!autoCommit && dirty) || force;
  }

  //把参数包装成Collection
  private Object wrapCollection(final Object object) {
    //参数如果是Collection类型，做collection标记
    if (object instanceof Collection) {
      //map类型为自定义严格map --> StrictMap
      StrictMap<Object> map = new StrictMap<>();
      map.put("collection", object);
      //参数如果是List类型，再做list标记
      if (object instanceof List) {
        map.put("list", object);
      }
      return map;
    } else if (object != null && object.getClass().isArray()) {
      StrictMap<Object> map = new StrictMap<>();
      //参数如果是数组类型（Array.isArray()），做array标记
      map.put("array", object);
      return map;
    }
    //如果参数不属于集合和数组，原样返回
    return object;
  }

  //严格的Map
  public static class StrictMap<V> extends HashMap<String, V> {

    private static final long serialVersionUID = -5741767162221585340L;

    @Override
    public V get(Object key) {
      //如果找不到对应的key，直接抛BindingException异常，而不是返回null
      if (!super.containsKey(key)) {
        throw new BindingException("Parameter '" + key + "' not found. Available parameters are " + this.keySet());
      }
      return super.get(key);
    }

  }

}
