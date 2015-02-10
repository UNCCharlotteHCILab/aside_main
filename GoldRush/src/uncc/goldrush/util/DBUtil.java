package uncc.goldrush.util;

import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtil {
  private static SqlSessionFactory sqlMapper;
  private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);
  
  static {
    try {
      String resource = "uncc/goldrush/iBATISConfiguration.xml";
      logger.info("Loading iBATIS configuration from {}", resource);
      Reader reader = Resources.getResourceAsReader(resource);
      sqlMapper = new SqlSessionFactoryBuilder().build(reader);
    } catch (Exception ex) {
      // If this happens, I don't think we can continue.  Must
      // thow RuntimeException
      logger.error("Cannot configure iBATIS {}", ex);
      throw new RuntimeException("Cannot configure iBATIS", ex);
    }
  }
  
  public static SqlSessionFactory getSqlMapper() {
    return sqlMapper;
  }
  
  private DBUtil() {}
}
