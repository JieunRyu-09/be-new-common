package kr.co.triphos.manage.dao;

import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import javax.annotation.Resource;
import java.util.List;

@Log4j2
public abstract class AbstractDAO extends SqlSessionDaoSupport {

	@Resource(name = "sqlSession")
	public void setSqlSessionFactory(SqlSessionFactory sqlSession) {
		super.setSqlSessionFactory(sqlSession);
	}

	/**
	 * insert query 실행
	 *
	 * @param queryId SQL mapping query ID
	 * @return 적용 결과수
	 */
	public int insert(String queryId) {
//		log.debug("insert | {}", queryId);
		return getSqlSession().insert(queryId);
	}

	/**
	 * insert query 실행
	 *
	 * @param queryId SQL mapping query ID
	 * @param parameterObject Parameter 객체(VO, Map)
	 * @return 적용 결과수
	 */
	public int insert(String queryId, Object parameterObject) {
//		log.debug("insert | {}", queryId);
		return getSqlSession().insert(queryId, parameterObject);
	}

	/**
	 * update query 실행
	 *
	 * @param queryId SQL mapping query ID
	 * @return 적용 결과수
	 */
	public int update(String queryId) {
//		log.debug("update | {}", queryId);
		return getSqlSession().update(queryId);
	}

	/**
	 * update query 실행
	 *
	 * @param queryId SQL mapping query ID
	 * @param parameterObject Parameter 객체(VO, Map)
	 * @return 적용 결과수
	 */
	public int update(String queryId, Object parameterObject) {
//		log.debug("update | {}", queryId);
		return getSqlSession().update(queryId, parameterObject);
	}

	/**
	 * delete query 실행
	 *
	 * @param queryId SQL mapping query ID
	 * @return 적용 결과수
	 */
	public int delete(String queryId) {
//		log.debug("delete | {}", queryId);
		return getSqlSession().delete(queryId);
	}

	/**
	 * delete query 실행
	 *
	 * @param queryId SQL mapping query ID
	 * @param parameterObject Parameter 객체(VO, Map)
	 * @return 적용 결과수
	 */
	public int delete(String queryId, Object parameterObject) {
//		log.debug("delete | {}", queryId);
		return getSqlSession().delete(queryId, parameterObject);
	}

	/**
	 * 단건조회 query 실행
	 *
	 * @param queryId SQL mapping query ID
	 * @return 결과 객체 - SQL mapping 파일에서 지정한 resultType/resultMap 에 의한 단일 결과 객체
	 */
	public <T> T selectOne(String queryId) {
//		log.debug("selectOne | {}", queryId);
		return getSqlSession().selectOne(queryId);
	}

	/**
	 * 단건조회 query 실행
	 *
	 * @param queryId SQL mapping query ID
	 * @param parameterObject Parameter 객체(VO, Map)
	 * @return 결과 객체 - SQL mapping 파일에서 지정한 resultType/resultMap 에 의한 단일 결과 객체
	 */
	public <T> T selectOne(String queryId, Object parameterObject) {
//		log.debug("selectOne | {}", queryId);
		return getSqlSession().selectOne(queryId, parameterObject);
	}

	/**
	 * 리스트 조회 query 실행
	 *
	 * @param queryId SQL mapping query ID
	 * @return 결과 List 객체 - SQL mapping 파일에서 지정한  resultType/resultMap 에 의한 결과 객체(VO, Map)의 List
	 */
	public <E> List<E> selectList(String queryId) {
//		log.debug("selectList | {}", queryId);
		return getSqlSession().selectList(queryId);
	}

	/**
	 * 리스트 조회 query 실행
	 *
	 * @param queryId SQL mapping query ID
	 * @param parameterObject Parameter 객체(VO, Map)
	 * @return 결과 List 객체 - SQL mapping 파일에서 지정한  resultType/resultMap 에 의한 결과 객체(VO, Map)의 List
	 */
	public <E> List<E> selectList(String queryId, Object parameterObject) {
//		log.debug("selectList | {}", queryId);
		return getSqlSession().selectList(queryId, parameterObject);
	}
}
