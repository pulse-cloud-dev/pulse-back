package pulse.back.domain.mentoring.dto;

import pulse.back.common.repository.ItemRepository;
import pulse.back.common.repository.MetaRepository;
import pulse.back.common.util.MyDateUtils;
import pulse.back.entity.common.Item;
import pulse.back.entity.common.Meta;
import pulse.back.entity.member.Member;
import pulse.back.entity.mento.AcademicInfo;
import pulse.back.entity.mento.CareerInfo;
import pulse.back.entity.mento.CertificateInfo;
import pulse.back.entity.mento.MentoInfo;
import pulse.back.entity.mentoring.Mentoring;
import pulse.back.entity.s3.MemberDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public record GetMentoInfoDetailResponseDto(
        // 멘토링 횟수
        int mentoringCount,

        // 멘토 소개글
        String introduction,

        // 자격증 정보
        List<CertificateInfo> certificateInfoList,

        //학력정보
        List<AcademicInfo> academicInfoList,

        //선호지역
        List<String> preferredLocations,

        // 멘토링 리스트
        List<GetMentoringListResponseDto> mentoringList
) {
    public static GetMentoInfoDetailResponseDto of(
            MentoInfo mentoInfo,
            List<GetMentoringListResponseDto> mentoringList,
            ItemRepository itemRepository,
            MetaRepository metaRepository
    ) {
        int mentorCareerTotalYear = 0;

        if (mentoInfo.careerInfo() != null && !mentoInfo.careerInfo().isEmpty()) {
            CareerInfo latestCareer = mentoInfo.careerInfo().stream()
                    .max(Comparator.comparing(CareerInfo::joinDate))
                    .orElse(null);

            long totalMonths = 0;
            for (CareerInfo careerInfo : mentoInfo.careerInfo()) {
                if (careerInfo.joinDate() != null) {
                    LocalDateTime joinDate = MyDateUtils.fromString(careerInfo.joinDate());
                    LocalDateTime retireDate;

                    if (careerInfo.isWorking()) {
                        retireDate = LocalDateTime.now();
                    } else {
                        retireDate = MyDateUtils.fromString(careerInfo.retireDate());
                    }

                    if (joinDate != null && retireDate != null) {
                        totalMonths += ChronoUnit.MONTHS.between(joinDate, retireDate);
                    }
                }
            }
            mentorCareerTotalYear = (int)(totalMonths / 12) + 1;
        }

        List<String> preferredAreaNames;

        if (mentoInfo.preferredAreas() == null || mentoInfo.preferredAreas().isEmpty()) {
            preferredAreaNames = List.of();
        } else {
            preferredAreaNames = mentoInfo.preferredAreas().stream()
                    .map(code -> {
                        // Mono<Item>를 block()으로 동기 처리
                        Item item = itemRepository.findByCode(code).block();
                        if (item != null) {
                            return item.name();
                        }
                        Meta meta = metaRepository.findByCode(code).block();
                        if (meta != null) {
                            return meta.name();
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .toList();
        }

        return new GetMentoInfoDetailResponseDto(
                mentoringList != null ? mentoringList.size() : 0,
                mentoInfo.mentorIntroduction(),
                mentoInfo.certificateInfo() != null && !mentoInfo.certificateInfo().isEmpty() ? mentoInfo.certificateInfo() : null,
                mentoInfo.academicInfo() != null && !mentoInfo.academicInfo().isEmpty() ? mentoInfo.academicInfo() : null,
                preferredAreaNames,
                mentoringList != null ? mentoringList : List.of()
        );
    }



}
