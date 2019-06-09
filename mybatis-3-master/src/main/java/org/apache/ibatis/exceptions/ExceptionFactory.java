/**
 *    Copyright 2009-2015 the original author or authors.
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
package org.apache.ibatis.exceptions;

import org.apache.ibatis.executor.ErrorContext;

/**
 * 异常工厂
 * @author Clinton Begin
 */
public class ExceptionFactory {

  private ExceptionFactory() {
    // Prevent Instantiation
  }

  //异常包装方法，将异常包装成mybatis自己的PersistenceException
  public static RuntimeException wrapException(String message, Exception e) {
    //每个线程都会有一个ErrorContext，所以可以得到，  .message(message).cause是典型的构建器模式
    //查找错误上下文，得到错误原因，传给PersistenceException
    return new PersistenceException(ErrorContext.instance().message(message).cause(e).toString(), e);
  }

}
