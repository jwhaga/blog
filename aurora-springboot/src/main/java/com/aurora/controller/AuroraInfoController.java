package com.aurora.controller;

import com.aurora.annotation.OptLog;
import com.aurora.enums.FilePathEnum;
import com.aurora.model.dto.AboutDTO;
import com.aurora.model.dto.AuroraAdminInfoDTO;
import com.aurora.model.dto.AuroraHomeInfoDTO;
import com.aurora.model.dto.WebsiteConfigDTO;
import com.aurora.model.vo.AboutVO;
import com.aurora.model.vo.ResultVO;
import com.aurora.model.vo.WebsiteConfigVO;
import com.aurora.service.AuroraInfoService;
import com.aurora.strategy.context.UploadStrategyContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import static com.aurora.constant.OptTypeConstant.UPDATE;
import static com.aurora.constant.OptTypeConstant.UPLOAD;

@Tag(name = "aurora信息")
@RestController
public class AuroraInfoController {

    @Autowired
    private AuroraInfoService auroraInfoService;

    @Autowired
    private UploadStrategyContext uploadStrategyContext;

    @Operation(summary = "上报访客信息")
    @PostMapping("/report")
    public ResultVO<?> report() {
        auroraInfoService.report();
        return ResultVO.ok();
    }

    @Operation(summary = "获取系统信息")
    @GetMapping("/")
    public ResultVO<AuroraHomeInfoDTO> getBlogHomeInfo() {
        return ResultVO.ok(auroraInfoService.getAuroraHomeInfo());
    }

    @Operation(summary = "获取系统后台信息")
    @GetMapping("/admin")
    public ResultVO<AuroraAdminInfoDTO> getBlogBackInfo() {
        return ResultVO.ok(auroraInfoService.getAuroraAdminInfo());
    }

    @OptLog(optType = UPDATE)
    @Operation(summary = "更新网站配置")
    @PutMapping("/admin/website/config")
    public ResultVO<?> updateWebsiteConfig(@Valid @RequestBody WebsiteConfigVO websiteConfigVO) {
        auroraInfoService.updateWebsiteConfig(websiteConfigVO);
        return ResultVO.ok();
    }

    @Operation(summary = "获取网站配置")
    @GetMapping("/admin/website/config")
    public ResultVO<WebsiteConfigDTO> getWebsiteConfig() {
        return ResultVO.ok(auroraInfoService.getWebsiteConfig());
    }

    @Operation(summary = "查看关于我信息")
    @GetMapping("/about")
    public ResultVO<AboutDTO> getAbout() {
        return ResultVO.ok(auroraInfoService.getAbout());
    }

    @OptLog(optType = UPDATE)
    @Operation(summary = "修改关于我信息")
    @PutMapping("/admin/about")
    public ResultVO<?> updateAbout(@Valid @RequestBody AboutVO aboutVO) {
        auroraInfoService.updateAbout(aboutVO);
        return ResultVO.ok();
    }

    @OptLog(optType = UPLOAD)
    @Operation(summary = "上传博客配置图片")
    @PostMapping("/admin/config/images")
    public ResultVO<String> savePhotoAlbumCover(
            @Parameter(description = "图片", required = true)
            MultipartFile file) {
        return ResultVO.ok(uploadStrategyContext.executeUploadStrategy(file, FilePathEnum.CONFIG.getPath()));
    }

}
