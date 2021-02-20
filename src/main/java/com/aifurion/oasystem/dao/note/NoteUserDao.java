package com.aifurion.oasystem.dao.note;

import com.aifurion.oasystem.entity.note.Noteuser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/20 10:34
 */
@Repository
public interface NoteUserDao extends PagingAndSortingRepository<Noteuser, Long> {

	@Query("select n.id from Noteuser n where n.noteId=?1 and n.userId=?2")
	Long findId(long noteid,long userid);
}
