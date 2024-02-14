package com.qianqiu.clouddisk.service;

import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import com.qianqiu.clouddisk.model.dto.*;
import com.qianqiu.clouddisk.utils.commonResult.CommonPage;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileInfoService {
    CommonResult<CommonPage<FileInfo>> getFileInfoList(FileInfoDTO fileInfoDTO);
    int saveAvatarToDB(MinioUploadDTO minioUploadDto, String userId, String fileMd5);

    CommonResult<FileInfo> createNewFolder(FolderDTO folderDTO);

    CommonResult renameFile(RenameFileDTO renameFileDTO);

    CommonResult uploadFile(String fileName, String fileMd5, Integer chunkIndex, Integer chunks, String fileId, String filePid, MultipartFile file, Long sourceFileSize, String sourcefileType);

    UpLoadSliceFileDTO initUploadFile(String fileName, String filePid, Integer chunks, Long sourceFileSize, String sourceFileMd5, MultipartFile file, String sourceFileType);

    CommonResult delFileListByIds(List<String> fileIds);

    CommonResult delFileById(String fileId);

    CommonResult selectAllFolder(SelectAllFolderDTO selectAllFolderDTO);

    CommonResult moveFileToFolderById(MoveFileToFolderByIdDTO moveFileToFolderByIdDTO);

    CommonResult moveFileListToFolderByIds(MoveFileListToFolderByIdsDTO moveFileListToFolderByIdsDTO);

    CommonResult getFolderInfo(GetFolderInfoDTO getFolderInfoDTO);
}
