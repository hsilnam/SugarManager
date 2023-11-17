package kr.co.sugarmanager.business.timeline.service;

import kr.co.sugarmanager.business.timeline.dto.TimelineDateDTO;
import kr.co.sugarmanager.business.timeline.dto.TimelineMonthDTO;

public interface TimelineService {
    TimelineMonthDTO.Response timelineMonth(Long pk, String nickname, Integer year, Integer month);
    TimelineDateDTO.Response timelineDate(Long pk, String nickname, Integer year, Integer month, Integer date);
}
