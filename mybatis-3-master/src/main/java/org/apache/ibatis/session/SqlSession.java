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
package org.apache.ibatis.session;

import java.io.Closeable;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;

/**
 * 这是MyBatis主要的一个类，用来执行SQL，获取映射器，管理事务。
 * 通常情况下，我们在应用程序中使用的Mybatis的API就是这个接口定义的方法。
 *
 * The primary Java interface for working with MyBatis.
 * Through this interface you can execute commands, get mappers and manage transactions.
 *
 * @author Clinton Begin
 */
public interface SqlSession extends Closeable {

  /**
   * 根据指定的sqlId获取一条记录的封装对象。
   * Retrieve a single row mapped from the statement key.
   * @param <T> the returned object type 封装之后的对象类型
   * @param statement sqlId
   * @return Mapped object 封装的对象
   */
  <T> T selectOne(String statement);

  /**
   * 根据指定的sqlId和 包含参数的对象 获取一条记录的封装对象。
   * 一般在实际使用中，这个参数传递的是pojo，或者Map或者ImmutableMap。
   * Retrieve a single row mapped from the statement key and parameter.
   * @param <T> the returned object type
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @return Mapped object
   */
  <T> T selectOne(String statement, Object parameter);

  /**
   * 根据指定的sqlId获取多条记录。
   * Retrieve a list of mapped objects from the statement key and parameter.
   * @param <E> the returned list element type
   * @param statement Unique identifier matching the statement to use.
   * @return List of mapped object
   */
  <E> List<E> selectList(String statement);

  /**
   * 根据指定的sqlId和 包含参数的对象 获取多条记录。
   * Retrieve a list of mapped objects from the statement key and parameter.
   * @param <E> the returned list element type
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @return List of mapped object
   */
  <E> List<E> selectList(String statement, Object parameter);

  /**
   * 根据指定的sqlId和 包含参数的对象 获取多条记录，该方法容许我们进行分页查询。
   *
   * 需要注意的是默认情况下，Mybatis为了扩展性，仅仅支持内存分页。也就是会先把
   * 所有的数据查询出来以后，然后在内存中进行分页。
   * Retrieve a list of mapped objects from the statement key and parameter,
   * within the specified row bounds.
   * @param <E> the returned list element type
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @param rowBounds  Bounds to limit object retrieval
   * @return List of mapped object 分页查询的参数（起始页，每页大小）
   */
  <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds);

  /**
   * 根据sqlId将查询到的结果列表转换为Map类型。
   * The selectMap is a special case in that it is designed to convert a list
   * of results into a Map based on one of the properties in the resulting
   * objects.
   * Eg. Return a of Map[Integer,Author] for selectMap("selectAuthors","id")
   * @param <K> the returned Map keys type
   * @param <V> the returned Map values type
   * @param statement Unique identifier matching the statement to use. 查询条件
   * @param mapKey The property to use as key for each value in the list. 该参数作为返回结果map的key
   * @return Map containing key pair data.
   */
  <K, V> Map<K, V> selectMap(String statement, String mapKey);

  /**
   * 根据指定的sqlId和 包含参数的对象查询，并将结果转换为Map。
   * The selectMap is a special case in that it is designed to convert a list
   * of results into a Map based on one of the properties in the resulting
   * objects.
   * @param <K> the returned Map keys type
   * @param <V> the returned Map values type
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @param mapKey The property to use as key for each value in the list.
   * @return Map containing key pair data.
   */
  <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey);

  /**
   * 根据指定的sqlId和 包含参数的对象查询，并将结果转换为map，该方法容许我们进行分页查询。
   * The selectMap is a special case in that it is designed to convert a list
   * of results into a Map based on one of the properties in the resulting
   * objects.
   * @param <K> the returned Map keys type
   * @param <V> the returned Map values type
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @param mapKey The property to use as key for each value in the list.
   * @param rowBounds  Bounds to limit object retrieval
   * @return Map containing key pair data.
   */
  <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds);

  /**
   * 当查询百万级的数据的时候，使用游标可以节省内存的消耗，不需要一次性取出所有数据，可以进行逐条处理或逐条取出部分批量处理。
   * 游标提供与列表相同的结果，不同之处在于它使用迭代器懒加载提取数据。
   * A Cursor offers the same results as a List, except it fetches data lazily using an Iterator.
   * @param <T> the returned cursor element type.
   * @param statement Unique identifier matching the statement to use.
   * @return Cursor of mapped objects
   */
  <T> Cursor<T> selectCursor(String statement);

  /**
   * 根据指定的sqlId和 包含参数的对象查询，返回游标结果。
   * A Cursor offers the same results as a List, except it fetches data lazily using an Iterator.
   * @param <T> the returned cursor element type.
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @return Cursor of mapped objects
   */
  <T> Cursor<T> selectCursor(String statement, Object parameter);

  /**
   * 根据指定的sqlId和 包含参数的对象查询，返回游标结果，该方法容许我们进行分页查询。
   * A Cursor offers the same results as a List, except it fetches data lazily using an Iterator.
   * @param <T> the returned cursor element type.
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @param rowBounds  Bounds to limit object retrieval
   * @return Cursor of mapped objects
   */
  <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds);

  /**
   * 根据指定的sqlId和 包含参数的对象查询，获取一条记录,并转交给ResultHandler处理。这个方法容许我们自己定义对
   * 查询到的行的处理方式。
   * --不常用
   * Retrieve a single row mapped from the statement key and parameter
   * using a {@code ResultHandler}.
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @param handler ResultHandler that will handle each retrieved row
   */
  void select(String statement, Object parameter, ResultHandler handler);

  /**
   * 根据指定的sqlId查询，获取一条记录,并转交给ResultHandler处理。
   * Retrieve a single row mapped from the statement
   * using a {@code ResultHandler}.
   * @param statement Unique identifier matching the statement to use.
   * @param handler ResultHandler that will handle each retrieved row
   */
  void select(String statement, ResultHandler handler);

  /**
   * 根据指定的sqlId和 包含参数的对象查询，获取一条记录,并转交给ResultHandler处理。该方法容许我们进行分页查询。
   * Retrieve a single row mapped from the statement key and parameter
   * using a {@code ResultHandler} and {@code RowBounds}.
   * @param statement Unique identifier matching the statement to use.
   * @param rowBounds RowBound instance to limit the query results
   * @param handler ResultHandler that will handle each retrieved row
   */
  void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler);

  /**
   * 插入，执行insert语句。insert into user(id,age,name) value('1',12,'Richard');
   * Execute an insert statement.
   * @param statement Unique identifier matching the statement to execute.
   * @return int The number of rows affected by the insert. 返回受影响行数
   */
  int insert(String statement);

  /**
   * 插入，用给定参数对象执行insert语句。任何生成的autoincrement值或selectKey条目将修改给定的参数对象属性。返回受影响的行数。
   * Execute an insert statement with the given parameter object. Any generated
   * autoincrement values or selectKey entries will modify the given parameter
   * object properties. Only the number of rows affected will be returned.
   * @param statement Unique identifier matching the statement to execute.
   * @param parameter A parameter object to pass to the statement.
   * @return int The number of rows affected by the insert.
   */
  int insert(String statement, Object parameter);

  /**
   * 执行update语句。返回受影响的行数。
   * Execute an update statement. The number of rows affected will be returned.
   * @param statement Unique identifier matching the statement to execute.
   * @return int The number of rows affected by the update.
   */
  int update(String statement);

  /**
   * 用给定参数对象执行update语句。返回受影响的行数。
   * Execute an update statement. The number of rows affected will be returned.
   * @param statement Unique identifier matching the statement to execute.
   * @param parameter A parameter object to pass to the statement.
   * @return int The number of rows affected by the update.
   */
  int update(String statement, Object parameter);

  /**
   * 删除记录，返回受影响的行数。
   * Execute a delete statement. The number of rows affected will be returned.
   * @param statement Unique identifier matching the statement to execute.
   * @return int The number of rows affected by the delete.
   */
  int delete(String statement);

  /**
   * 用给定参数对象删除记录。
   * Execute a delete statement. The number of rows affected will be returned.
   * @param statement Unique identifier matching the statement to execute.
   * @param parameter A parameter object to pass to the statement.
   * @return int The number of rows affected by the delete.
   */
  int delete(String statement, Object parameter);

//以下是事务控制方法，commit rollback
  /**
   * 提交事务
   * ① 刷新批量语句，并提交数据库的连接。
   * ② 注意，如果没有没有update，delete，insert调用的时候，不会提交连接。
   * ③ 如果要强制提交，调用commit(true)。
   * Flushes batch statements and commits database connection.
   * Note that database connection will not be committed if no updates/deletes/inserts were called.
   * To force the commit call {@link SqlSession#commit(boolean)}
   */
  void commit();

  /**
   * 提交事务
   * 根据传入boolean值判断是否需要强制提交。
   * Flushes batch statements and commits database connection.
   * @param force forces connection commit
   */
  void commit(boolean force);

  /**
   * 回滚事务
   * ① 放弃挂起的批处理语句并将数据库连接回滚。
   * ② 注意，如果没有update，delete，insert被调用，数据库连接将不会回滚。
   * ③ 强制回滚，调用rollback(true)
   * Discards pending batch statements and rolls database connection back.
   * Note that database connection will not be rolled back if no updates/deletes/inserts were called.
   * To force the rollback call {@link SqlSession#rollback(boolean)}
   */
  void rollback();

  /**
   * 回滚事务
   * 根据传入boolean值判断是否需要强制回滚。
   * Discards pending batch statements and rolls database connection back.
   * Note that database connection will not be rolled back if no updates/deletes/inserts were called.
   * @param force forces connection rollback
   */
  void rollback(boolean force);

  /**
   * 刷新批处理语句,返回批处理结果。
   * Flushes batch statements.
   * @return BatchResult list of updated records
   * @since 3.0.6
   */
  List<BatchResult> flushStatements();

  /**
   * 关闭Session
   * Closes the session.
   */
  @Override
  void close();

  /**
   * 清理本地Session缓存
   * Clears local session cache.
   */
  void clearCache();

  /**
   * 获取当前配置
   * Retrieves current configuration.
   * @return Configuration
   */
  Configuration getConfiguration();

  /**
   * 获取一个映射器
   * 使用了泛型，使得类型安全
   * Retrieves a mapper.
   * @param <T> the mapper type
   * @param type Mapper interface class
   * @return a mapper bound to this SqlSession
   */
  <T> T getMapper(Class<T> type);

  /**
   * 得到数据库连接
   * Retrieves inner database connection.
   * @return Connection
   */
  Connection getConnection();
}
