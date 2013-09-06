package ca.delmar.api.controller;

import ca.delmar.api.domain.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jinw
 * Date: 06/09/13
 * Time: 2:17 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/notices")
public class NoticeController {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @RequestMapping(value = "/list")
    @ResponseBody
    public List<Notice> getList(@RequestParam("lang") String language, @RequestParam("count") int count) {
        int languageId = language.equalsIgnoreCase("en") ? 1 : 2;
        jdbcTemplate.setMaxRows(count);
        String query =
                "SELECT n.* " +
                        "  FROM TBLNOTICE n " +
                        "  WHERE n.LANGUAGEID = :languageId " +
                        "AND n.ISACTIVE       = 1 " +
                        "AND n.ISTEMPLATE     = 0 " +
                        "AND n.NOTICEPRIVATE  = 0 " +
                        "AND n.NOTICEDRAFT    = 0 " +
                        "AND n.DATESENT      IS NOT NULL " +
                        "ORDER BY n.DATESENT DESC";
        List<Notice> list = jdbcTemplate.query(query, new Object[]{languageId}, new NoticeMapper());
        jdbcTemplate.setMaxRows(0);
        return list;
    }

    private static final class NoticeMapper implements RowMapper<Notice> {
        public Notice mapRow(ResultSet rs, int rowNum) throws SQLException {
            Notice result = new Notice();
            result.id = rs.getString("id");
            result.title = rs.getString("title");
            result.html = rs.getString("html");
            return result;
        }
    }

}
