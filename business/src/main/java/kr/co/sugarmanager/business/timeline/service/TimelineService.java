package kr.co.sugarmanager.business.timeline.service;

import kr.co.sugarmanager.business.timeline.dto.TimelineDateDTO;
import kr.co.sugarmanager.business.timeline.dto.TimelineMonthDTO;

public interface TimelineService {
    TimelineMonthDTO.Response timelineMonth( String nickname, Integer year, Integer month);
// Long id, String nickname, Integer year, Integer month);

    TimelineDateDTO.Response timelineDate(String nickname, Integer year, Integer month, Integer date);

//    TimelineDateDTO.Response timelineDate(Long id, String nickname, Integer year, Integer month, Integer date);
// Long id, String nickname, Integer year, Integer month);
}
