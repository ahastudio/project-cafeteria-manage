package com.poppo.dallab.cafeteria.interfaces;

import com.poppo.dallab.cafeteria.adapters.Mapper;
import com.poppo.dallab.cafeteria.applications.MenuPlanService;
import com.poppo.dallab.cafeteria.applications.MenuService;
import com.poppo.dallab.cafeteria.applications.WorkDayService;
import com.poppo.dallab.cafeteria.domain.Menu;
import com.poppo.dallab.cafeteria.domain.WorkDay;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MenuPlanController {

    private final WorkDayService workDayService;
    private final MenuService menuService;
    private final MenuPlanService menuPlanService;
    private final Mapper mapper;

    @GetMapping("/workDay")
    public List<MenuPlanResponseDto> getList() {

        List<WorkDay> workDays = workDayService.getWorkWeekFromNow();

        List<MenuPlanResponseDto> menuPlanResponseDtos = workDays.stream()
                .map(workDay -> MenuPlanResponseDto.builder()
                        .date(workDay.getDate())
                        .day(workDay.getDay())
                        .menus(menuService.getMenusByWorkDayId(workDay.getId()))
                        .build())
                .collect(Collectors.toList());

        return menuPlanResponseDtos;

    }

    @GetMapping("/workDay/2019-09-30")
    public MenuPlanResponseDto getOne() {

        WorkDay workDay = workDayService.getWorkDayByString("2019-09-30");
        List<Menu> menus = menuService.getMenusByWorkDayId(workDay.getId());

        MenuPlanResponseDto menuPlanResponseDto = MenuPlanResponseDto.builder()
                .date(workDay.getDate())
                .day(workDay.getDay())
                .menus(menus)
                .build();

        return menuPlanResponseDto;

    }

    @PostMapping("/workDay/{date}/menuPlans")
    public ResponseEntity bulkCreate(
            @PathVariable(name = "date") String date,
            @RequestBody List<MenuPlanRequestDto> menuPlanRequestDtos
        ) throws URISyntaxException {

        String url = "/workDay/"+ date +"/menuPlans";

        List<Menu> menus = menuPlanRequestDtos.stream()
                .map(menuPlanRequestDto -> mapper.mapping(menuPlanRequestDto, Menu.class))
                .collect(Collectors.toList());

        menuPlanService.addBulkMenu(date, menus);

        return ResponseEntity.created(new URI(url)).body("{}");

    }

}
