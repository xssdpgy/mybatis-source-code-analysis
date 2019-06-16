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
package org.apache.ibatis.cursor;

import java.io.Closeable;

/**
 * Cursor contract to handle fetching items lazily using an Iterator.
 * Cursors are a perfect fit to handle millions of items queries that would not normally fits in memory.
 * If you use collections in resultMaps then cursor SQL queries must be ordered (resultOrdered="true")
 * using the id columns of the resultMap.
 *
 * @author Guillaume Darmont / guillaume@dropinocean.com
 */
public interface Cursor<T> extends Closeable, Iterable<T> {

  /**
   * 如果游标已开始从数据库中获取项目，则为true。
   * @return true if the cursor has started to fetch items from database.
   */
  boolean isOpen();

  /**
   * 如果游标已完全使用并返回了与查询匹配的所有元素，则返回true。
   * @return true if the cursor is fully consumed and has returned all elements matching the query.
   */
  boolean isConsumed();

  /**
   * 获取当前项目索引。第一项的索引为0。如果尚未检索到第一个游标项，则返回-1。
   * Get the current item index. The first item has the index 0.
   * @return -1 if the first cursor item has not been retrieved. The index of the current item retrieved.
   */
  int getCurrentIndex();
}
