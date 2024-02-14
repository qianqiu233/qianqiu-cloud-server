package com.qianqiu.clouddisk.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.qianqiu.clouddisk.exception.CommonException;
import com.qianqiu.clouddisk.mbg.mbg_mapper.FileInfoMapper;
import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import com.qianqiu.clouddisk.mbg.mbg_model.FileInfoExample;
import com.qianqiu.clouddisk.model.dto.*;
import com.qianqiu.clouddisk.service.FileInfoService;
import com.qianqiu.clouddisk.service.MinioService;
import com.qianqiu.clouddisk.service.UserService;
import com.qianqiu.clouddisk.utils.*;
import com.qianqiu.clouddisk.utils.Regexs.RegexUtils;
import com.qianqiu.clouddisk.utils.commonResult.CommonPage;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import com.qianqiu.clouddisk.utils.enums.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.*;
import static com.qianqiu.clouddisk.utils.enums.FileUseFlagEnums.*;

@Service
@Slf4j
public class FileInfoServiceImpl implements FileInfoService {
    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MinioService minioService;
    @Resource
    private FileInfoMapper fileInfoMapper;
    @Autowired
    private FFmepgUtil ffmpegUtil;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public CommonResult<CommonPage<FileInfo>> getFileInfoList(FileInfoDTO fileInfoDTO) {
        String userId = UserThreadLocal.getUserId();
        Integer pageNum = fileInfoDTO.getPageNum();
        Integer pageSize = fileInfoDTO.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        FileInfoExample fileInfoExample = new FileInfoExample();
        FileInfoExample.Criteria criteria = fileInfoExample.createCriteria();
        String fileName = fileInfoDTO.getFileName();
        String category = fileInfoDTO.getCategory();
        String filePid = fileInfoDTO.getFilePid();
        String order = DEFAULT_SORT_FIELD + DEFAULT_DESC;
        if (fileName != null) {
            criteria.andFileNameLike("%" + fileName + "%");
        }
        if (category != null&&!"all".equals(category)) {
            FileCategoryEnums categoryByCategoryCode = FileAboutUtil.getCategoryByCategoryCode(category);
            criteria.andFileCategoryEqualTo(categoryByCategoryCode.getCategory());
        }
        if (filePid!=null){
            criteria.andFilePidEqualTo(filePid);
        }
        criteria.andUseFlagEqualTo(USING.getFlag());
        criteria.andUserIdEqualTo(userId);
        fileInfoExample.setOrderByClause(order);
        List<FileInfo> fileInfoList = fileInfoMapper.selectByExample(fileInfoExample);
        return CommonResult.success(CommonPage.restPage(fileInfoList));
    }

    private String generateFileId() {
        String fileId = IdUtil.simpleUUID();
        return fileId;
    }

    public int saveAvatarToDB(MinioUploadDTO minioUploadDto, String userId, String fileMd5) {
        String fileId = generateFileId();
        FileInfo fileInfo = BeanUtil.copyProperties(minioUploadDto, FileInfo.class);
        fileInfo.setFileId(fileId);
        fileInfo.setUserId(userId);
        fileInfo.setUseFlag(USEINGAVATAR.getFlag());
        fileInfo.setFolderType(FileFolderType.FILE.getFolderCode());
        fileInfo.setFileType(FileTypeEnums.IMAGE.getTypeCode());
        fileInfo.setFileMd5(fileMd5);
//        生成封面，上传头像的话，封面就是头像自己，也不用调整
        fileInfo.setFileCover(minioUploadDto.getFileUrl());
// todo 是否进入垃圾箱？，是否将之前头像使用状态修改
        return fileInfoMapper.insert(fileInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<FileInfo> createNewFolder(FolderDTO folderDTO) {
        // todo 有问题
        String filePid = folderDTO.getFilePid();
        String fileName = folderDTO.getFileName();
        Integer folderType = FileFolderType.DIRECTORY.getFolderCode();
        checkFileName(filePid, folderType, fileName);
        String fileId = generateFileId();
        String userId = UserThreadLocal.getUserId();
        // minio创建文件夹
        MinioUploadDTO minioUploadDTO = minioUtil.createFolder(fileName, userId);
        FileInfo fileInfo = BeanUtil.copyProperties(minioUploadDTO, FileInfo.class);
        fileInfo.setFileId(fileId);
        fileInfo.setUserId(userId);
        fileInfo.setFilePid(filePid);
        fileInfo.setFileName(fileName);
        fileInfo.setFolderType(folderType);
        fileInfo.setFileCategory(FileCategoryEnums.FOLDER.getCategory());
        fileInfo.setFileType(FileTypeEnums.FOLDER.getTypeCode());
        fileInfo.setUseFlag(USING.getFlag());
        fileInfo.setStatus(FileStatusEnum.TRANSCODING_SUCCESS.getCode());
        int count = fileInfoMapper.insert(fileInfo);
        if (count == 0) {
            throw new CommonException("新建文件夹失败");
        }
        return CommonResult.success(fileInfo, "新建文件夹成功");
    }

    @Override
    public CommonResult renameFile(RenameFileDTO renameFileDTO) {
//        todo 需要修改元数据？感觉不用，好麻烦，改下数据库名称就行了吧，直接指向对应的url更方便点
        String userId = UserThreadLocal.getUserId();
        String fileId = renameFileDTO.getFileId();
        String filePid = renameFileDTO.getFilePid();
        String fileName = renameFileDTO.getFileName();
        Integer folderType = FileFolderType.FILE.getFolderCode();
        checkFileName(filePid, folderType, fileName);
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
        fileInfo.setFilePid(filePid);
        fileInfo.setFileName(fileName);
        fileInfo.setLastUpdateTime(new Date());
        int count = fileInfoMapper.updateByPrimaryKeySelective(fileInfo);
        if (count == 0) {
            throw new CommonException("重命名文件失败");
        }
        return CommonResult.success(fileInfo, "重命名文件成功");

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult uploadFile(String fileName, String fileMd5, Integer chunkIndex,
                                   Integer chunks, String fileId, String filePid, MultipartFile file, Long sourceFileSize, String sourceFileType) {
        // todo 需要判断空间是否足够
        UpLoadSliceFileDTO upLoadFileStatus = new UpLoadSliceFileDTO();
        //初始化为正常上传中
        upLoadFileStatus.setUpLoadFileStatus(UpLoadFileStageStatus.SLICE.getUpLoadStatus());
        MinioUploadDTO minioUploadDTO = null;
        FileInfo fileInfo = null;
        String userId = UserThreadLocal.getUserId();
        String sourceFileId = fileMd5;
        if (DEFAULT_UPLOAD_START.equals(fileId) && chunkIndex == 0) {
            fileId = sourceFileId;
            //初始化
            upLoadFileStatus = initUploadFile(fileName, filePid, chunks, sourceFileSize, fileMd5, file, sourceFileType);
        }
        // 文件上传过，直接秒传
        if (UpLoadFileStageStatus.UPLOADED.getUpLoadStatus().equals(upLoadFileStatus.getUpLoadFileStatus())) {
            // todo 还需判断pid是不是一样的，
            Date date = new Date();
            FileInfoExample fileInfoExample = new FileInfoExample();
            fileInfoExample.createCriteria().andUserIdEqualTo(userId).andFileMd5EqualTo(fileMd5);
            List<FileInfo> fileInfoList = fileInfoMapper.selectByExample(fileInfoExample);
            FileInfo uploadedFileInfo = fileInfoList.get(0);
            //设置新的fileId,
            String newFileId = fileMd5 + "-" + DateUtil.format(date, DateUtil.HMS);
            uploadedFileInfo.setFileId(newFileId);
            uploadedFileInfo.setFileName(fileName);
            uploadedFileInfo.setCreateTime(date);
            uploadedFileInfo.setLastUpdateTime(date);
            //存入数据库
            int count = fileInfoMapper.insert(uploadedFileInfo);
            if (count == 0) {
                throw new CommonException("文件上传失败");
            }
            //更新用户空间
            Boolean isUpdate = updateSpace(Collections.singletonList(newFileId), userId, 0);
            if (!isUpdate){
                throw new CommonException("更新空间失败");
            }
            UpLoadSliceFileDTO upLoadSliceFileDTO = new UpLoadSliceFileDTO();
            upLoadSliceFileDTO.setFileId(newFileId);
            upLoadSliceFileDTO.setStatus(UploadStatusEnums.UPLOAD_SECONDS.getCode());
            upLoadSliceFileDTO.setUpLoadFileStatus(UpLoadFileStageStatus.UPLOADED.getUpLoadStatus());
            //秒传
            return CommonResult.success(upLoadSliceFileDTO);
        }
        //初始化失败
        if (UpLoadFileStageStatus.FAIL.getUpLoadStatus().equals(upLoadFileStatus.getUpLoadFileStatus())) {
            throw new CommonException("文件初始化失败");
        }
        //正常模式，切片上传
        if (UpLoadFileStageStatus.SLICE.getUpLoadStatus().equals(upLoadFileStatus.getUpLoadFileStatus())) {
            //上传之前先判断，缓存里是否存在文件分片，存在就返回成功
            //todo 开始上传文件，这边是统一分片上传，后面在改下前端,还需判断文件存在直接返回
            //上传期间出现错误就直接删除所有分片，且清空文件，重新上传
            minioUploadDTO = minioService.uploadSliceFile(file, userId, sourceFileId, chunkIndex, fileName, chunks);
            //将分片文件保存到数据库/缓存
            fileInfo = BeanUtil.copyProperties(minioUploadDTO, FileInfo.class);
            fileInfo.setFileId(sourceFileId + "-" + chunkIndex);
            fileInfo.setUserId(userId);
            fileInfo.setFolderType(FileFolderType.FILE.getFolderCode());
//        生成分片文件md5
            String chunkFileMd5 = FileAboutUtil.getMD5(file);
            fileInfo.setFileMd5(chunkFileMd5);
            fileInfo.setStatus(0);
            fileInfo.setUseFlag(USING.getFlag());
            //存入数据库/缓存
            stringRedisTemplate.opsForList().rightPush(sourceFileId, JSONUtil.toJsonStr(fileInfo));
        }
        if (minioUploadDTO.getUploadFlag() == -1 || UpLoadFileStageStatus.MERGE.getUpLoadStatus().equals(upLoadFileStatus.getUpLoadFileStatus())) {
            String chunkObjectName = sourceFileId + "-" + fileName;
            //开始合并文件
            FileInfo sourceFileInfo = fileInfoMapper.selectByPrimaryKey(sourceFileId, userId);
            Integer fileCategory = sourceFileInfo.getFileCategory();
            FileCategoryEnums codeByCategory = FileAboutUtil.getCodeByCategory(fileCategory);
            MinioUploadDTO minioComposeDTO = minioService.composeFile(userId, chunkObjectName, fileName, chunks, codeByCategory.getPackageName());
            String composeFileUrl = minioComposeDTO.getFileUrl();
            String composeFileName = FileAboutUtil.fileSuffixToPNG(minioComposeDTO.getFileName());
            sourceFileInfo.setFileUrl(composeFileUrl);
            sourceFileInfo.setFilePath(minioComposeDTO.getFilePath());
            sourceFileInfo.setLastUpdateTime(new Date());
            //合并成功，是图片，图片本身就是缩略图
            if (FileCategoryEnums.IMAGE.getCategory().equals(fileCategory)) {
                sourceFileInfo.setFileCover(composeFileUrl);
                sourceFileInfo.setStatus(FileStatusEnum.TRANSCODING_SUCCESS.getCode());
            }
            //合并成功，如果是视频，生成缩略图
            boolean isThumbnailSuccess = ffmpegUtil.generateVideoThumbnail(composeFileUrl, composeFileName);
            if (isThumbnailSuccess && FileCategoryEnums.VIDEO.getCategory().equals(fileCategory)) {
                //生成base64编码
                String thumbnailPath = DEFAULT_THUMBNAIL_PACKAGE + composeFileName;
                String composeThumbnailBase64 = FileAboutUtil.fileToBase64(thumbnailPath);
                //设置封面
                sourceFileInfo.setFileCover(composeThumbnailBase64);
                //删除临时缩略图  // todo 如果删除失败？不知道怎么办，设置指数？，当数量达到一定程度，整个文件夹删掉？
                FileAboutUtil.delThumbnailPackageFileByName(composeFileName);
            }
            if (minioComposeDTO.getUploadFlag() == -2) {
                sourceFileInfo.setUseFlag(USING.getFlag());
                sourceFileInfo.setStatus(FileStatusEnum.TRANSCODING_SUCCESS.getCode());
            }
            log.info("源文件上传成功|sourceFileInfo:{}", sourceFileInfo);
            int updateCount = fileInfoMapper.updateByPrimaryKeySelective(sourceFileInfo);
            if (updateCount == 0) {
                throw new CommonException("文件修改失败");
            }
            Boolean aBoolean = minioService.delSliceFile(userId, chunks, chunkObjectName);
            if (!aBoolean) {
                throw new CommonException("文件删除失败");
            }
            stringRedisTemplate.delete(sourceFileId);
            //更新用户空间
            Boolean isUpdate = updateSpace(Collections.singletonList(sourceFileId), userId, 0);
            if (!isUpdate){
                throw new CommonException("更新空间失败");
            }
            upLoadFileStatus.setFileId(sourceFileId);
            upLoadFileStatus.setStatus(UploadStatusEnums.UPLOAD_FINISH.getCode());
            upLoadFileStatus.setUpLoadFileStatus(UpLoadFileStageStatus.UPLOAD_SUCCESS.getUpLoadStatus());
            return CommonResult.success(upLoadFileStatus, "文件上传成功");
        }
        upLoadFileStatus.setFileId(fileId);
        upLoadFileStatus.setStatus(UploadStatusEnums.UPLOADING.getCode());
        upLoadFileStatus.setUpLoadFileStatus(UpLoadFileStageStatus.SLICE.getUpLoadStatus());
        return CommonResult.success(upLoadFileStatus, "第" + chunkIndex + "分片上传成功");
    }

    /**
     * 移出回收站
     *
     * @return
     */
    public Boolean moveOutRecycle(FileInfo fileInfo) {
        // todo 需要判断空间是否足够
        fileInfo.setUseFlag(USING.getFlag());
        fileInfo.setLastUpdateTime(new Date());
        int count = fileInfoMapper.updateByPrimaryKeySelective(fileInfo);
        if (count == 0) {
            return false;
        }
        return true;
    }

    /**
     * 文件上传初始化
     *
     * @param fileName
     * @param filePid
     * @param sourceFileSize
     * @param sourceFileMd5
     * @param file
     * @param sourceFileType
     * @return
     */
    @Override
    public UpLoadSliceFileDTO initUploadFile(String fileName, String filePid, Integer chunks, Long sourceFileSize, String sourceFileMd5, MultipartFile file, String sourceFileType) {
        //第一个分片获取到文件类型和分类数据
        FileCategoryEnums fileCategoryType = FileAboutUtil.getFileCategoryType(sourceFileType);
        String[] strings = FileAboutUtil.splitByLastDot(fileName);
        String fileSuffix = strings[1];
        FileTypeEnums fileType = FileAboutUtil.getFileTypeByCategory(fileCategoryType, fileSuffix);
        String userId = UserThreadLocal.getUserId();
        String fileId = sourceFileMd5;
        MinioUploadDTO minioUtilFolder = minioUtil.createFolder("slice", userId);
        FileInfo dbfileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
        //返回
        UpLoadSliceFileDTO upLoadSliceFileDTO = new UpLoadSliceFileDTO();
        //数据库存在该文件
        if (dbfileInfo != null) {
            upLoadSliceFileDTO = getUpLoadSliceFileStageByUseFlag(dbfileInfo);
            return upLoadSliceFileDTO;
        }
        //不存在该文件
        //初始化切片放入缓存
        Boolean hasKey = stringRedisTemplate.hasKey(fileId);
        if (Boolean.FALSE.equals(hasKey)) {
            //主文件为键，带入分片，id为1.分片id从2开始
            stringRedisTemplate.opsForList().rightPush(fileId, fileId);
        } else {
            //说明还没彻底上传完成,  还在上传中  ,  由于上传期间出现任何错误都会删除所有分片和数据库文件，所以如果还是存在，只有未合并一个结果
            upLoadSliceFileDTO.setFileId(fileId);
            upLoadSliceFileDTO.setStatus(UploadStatusEnums.UPLOADING.getCode());
            upLoadSliceFileDTO.setUpLoadFileStatus(UpLoadFileStageStatus.MERGE.getUpLoadStatus());
            //通知可以合并
            return upLoadSliceFileDTO;
        }

        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileId(fileId);
        fileInfo.setUserId(userId);
        fileInfo.setFileName(fileName);
        fileInfo.setFileSize(sourceFileSize);
        fileInfo.setFileMd5(sourceFileMd5);
        fileInfo.setFilePid(filePid);
        fileInfo.setFolderType(FileFolderType.FILE.getFolderCode());
        fileInfo.setCreateTime(new Date());
        fileInfo.setFileType(fileType.getTypeCode());
        fileInfo.setFileCategory(fileCategoryType.getCategory());
        int isSuccess = fileInfoMapper.insert(fileInfo);
        if (isSuccess == 0) {
            upLoadSliceFileDTO.setFileId(fileId);
            upLoadSliceFileDTO.setStatus(UploadStatusEnums.UPLOADING.getCode());
            upLoadSliceFileDTO.setUpLoadFileStatus(UpLoadFileStageStatus.FAIL.getUpLoadStatus());
            stringRedisTemplate.delete(fileId);
            //通知初始化失败
            return upLoadSliceFileDTO;
        }
        //一切正常
        upLoadSliceFileDTO.setFileId(fileId);
        upLoadSliceFileDTO.setStatus(UploadStatusEnums.UPLOADING.getCode());
        upLoadSliceFileDTO.setUpLoadFileStatus(UpLoadFileStageStatus.SLICE.getUpLoadStatus());
        return upLoadSliceFileDTO;
    }

    @Override
    public CommonResult delFileListByIds(List<String> fileIds) {
        String userId = UserThreadLocal.getUserId();
        FileInfoExample fileInfoExample = new FileInfoExample();
        //todo 如果是目录，直接删除，连带minio的都删，
        // todo 如果目录有东西，那就进回收站
        //做个逻辑删除就好，进回收站的啊
        fileInfoExample.createCriteria()
                .andFileIdIn(fileIds)
                .andUserIdEqualTo(userId)
                .andUseFlagEqualTo(USING.getFlag());
        FileInfo fileInfo = new FileInfo();
        fileInfo.setUseFlag(RECYCLE.getFlag());
        fileInfo.setLastUpdateTime(new Date());
        int count = fileInfoMapper.updateByExampleSelective(fileInfo, fileInfoExample);
        if (count == 0) {
            throw new CommonException("删除文件失败，请重试");
        }
        //更新空间

        Boolean isUpdate = updateSpace(fileIds, userId, 1);
        if (!isUpdate){
            throw new CommonException("空间更新失败");
        }
        return CommonResult.success(null, "删除成功");


    }

    @Override
    public CommonResult delFileById(String fileId) {
        //todo 如果是目录，直接删除，连带minio的都删，
        // todo 如果目录有东西，那就进回收站
        String userId = UserThreadLocal.getUserId();
        FileInfo fileInfo = new FileInfo();
        FileInfoExample fileInfoExample = new FileInfoExample();
        fileInfoExample.createCriteria().andFileIdEqualTo(fileId).andUserIdEqualTo(userId).andUseFlagEqualTo(USING.getFlag());
        fileInfo.setUseFlag(RECYCLE.getFlag());
        fileInfo.setLastUpdateTime(new Date());
        int count = fileInfoMapper.updateByExampleSelective(fileInfo, fileInfoExample);
        if (count == 0) {
            throw new CommonException("删除文件失败，请重试");
        }
        //更新空间
        Boolean isUpdate = updateSpace(Collections.singletonList(fileId), userId, 1);
        if (!isUpdate){
            throw new CommonException("空间更新失败");
        }
        return CommonResult.success(null, "删除成功");
    }

    @Override
    public CommonResult selectAllFolder(SelectAllFolderDTO selectAllFolderDTO) {
        String userId = UserThreadLocal.getUserId();
        FileInfoExample fileInfoExample = new FileInfoExample();
        String currentFileIds = selectAllFolderDTO.getCurrentFileIds();
        String[] splits = currentFileIds.split(",");
        List<String> currentFileIdList = Arrays.asList(splits);
        FileInfoExample.Criteria criteria = fileInfoExample.createCriteria();
        if (currentFileIds!=null){
            criteria.andFileIdIn(currentFileIdList);
        }
        criteria.andUserIdEqualTo(userId)
                .andFilePidEqualTo(selectAllFolderDTO.getFilePid())
                .andFolderTypeEqualTo(FileFolderType.DIRECTORY.getFolderCode())
                .andUseFlagEqualTo(USING.getFlag());
        List<FileInfo> fileInfoList = fileInfoMapper.selectByExample(fileInfoExample);
        return CommonResult.success(fileInfoList);
    }

    @Override
    public CommonResult moveFileToFolderById(MoveFileToFolderByIdDTO moveFileToFolderByIdDTO) {
        String filePid = moveFileToFolderByIdDTO.getFilePid();
        String fileId = moveFileToFolderByIdDTO.getFileId();
        String userId = UserThreadLocal.getUserId();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileId(fileId);
        fileInfo.setUserId(userId);
        fileInfo.setFilePid(filePid);
        fileInfo.setLastUpdateTime(new Date());
        int count = fileInfoMapper.updateByPrimaryKeySelective(fileInfo);
        if (count==0){
            throw new CommonException("文件移动失败，请重新尝试");
        }
        return CommonResult.success(null,"已移动到对应文件夹");
    }

    @Override
    public CommonResult moveFileListToFolderByIds(MoveFileListToFolderByIdsDTO moveFileListToFolderByIdsDTO) {
        String filePid = moveFileListToFolderByIdsDTO.getFilePid();
        List<String> fileIds = moveFileListToFolderByIdsDTO.getFileIds();
        System.out.println(fileIds);
        String userId = UserThreadLocal.getUserId();
        FileInfoExample fileInfoExample = new FileInfoExample();
        fileInfoExample.createCriteria().andUserIdEqualTo(userId).andFileIdIn(fileIds);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilePid(filePid);
        fileInfo.setLastUpdateTime(new Date());
        int count = fileInfoMapper.updateByExampleSelective(fileInfo, fileInfoExample);
        if (count==0){
            throw new CommonException("文件移动失败，请重新尝试");
        }
        return CommonResult.success(count,"已移动到对应文件夹");
    }

    @Override
    public CommonResult getFolderInfo(GetFolderInfoDTO getFolderInfoDTO) {
        String path = getFolderInfoDTO.getPath();
        String[] paths = path.split("/");
        List<String> pathList = Arrays.asList(paths);
        String userId = UserThreadLocal.getUserId();
        FileInfoExample fileInfoExample = new FileInfoExample();
        fileInfoExample.createCriteria()
                .andFileIdIn(pathList)
                .andUseFlagEqualTo(USING.getFlag())
                .andUserIdEqualTo(userId)
                .andFolderTypeEqualTo(FileFolderType.DIRECTORY.getFolderCode());
        List<FileInfo> fileInfoList = fileInfoMapper.selectByExample(fileInfoExample);
        return CommonResult.success(fileInfoList);
    }

    /**
     * 文件名校验
     *
     * @param filePid
     * @param folderType
     * @param fileName
     */
    public void checkFileName(String filePid, Integer folderType, String fileName) {
        boolean folderInvalid = RegexUtils.isFolderInvalid(fileName);
        if (!folderInvalid) {
            throw new CommonException("文件夹命名不符合规范,请参考Windows文件夹命名规则");
        }
        String userId = UserThreadLocal.getUserId();
        FileInfoExample fileInfoExample = new FileInfoExample();
        fileInfoExample.createCriteria()
                .andUserIdEqualTo(userId)
                .andFilePidEqualTo(filePid)
                .andFolderTypeEqualTo(folderType)
                .andFileNameEqualTo(fileName)
                .andUseFlagEqualTo(USING.getFlag());
        long count = fileInfoMapper.countByExample(fileInfoExample);
        if (count > 0) {
            throw new CommonException("此目录下已存在同名文件，请修改名称");
        }
    }

    public UpLoadSliceFileDTO getUpLoadSliceFileStageByUseFlag(FileInfo fileInfo) {
        UpLoadSliceFileDTO upLoadSliceFileDTO = new UpLoadSliceFileDTO();
        Integer useFlag = fileInfo.getUseFlag();
        String fileId = fileInfo.getFileId();
        String userId = fileInfo.getUserId();
        upLoadSliceFileDTO.setFileId(fileId);
        //数据库中有该文件且使用中 秒传
        if (USING.getFlag().equals(useFlag)) {
            upLoadSliceFileDTO.setStatus(UploadStatusEnums.UPLOAD_SECONDS.getCode());
            upLoadSliceFileDTO.setUpLoadFileStatus(UpLoadFileStageStatus.UPLOADED.getUpLoadStatus());
            //通知可以秒传
            return upLoadSliceFileDTO;
        }
        //todo 数据库中存在该文件，但是处于回收站内 先直接拿回来
        if (RECYCLE.getFlag().equals(useFlag)) {
            //移出回收站
            Boolean isMoveOut = moveOutRecycle(fileInfo);
            if (!isMoveOut) {
                upLoadSliceFileDTO.setUpLoadFileStatus(UpLoadFileStageStatus.FAIL.getUpLoadStatus());
                upLoadSliceFileDTO.setStatus(UploadStatusEnums.UPLOADING.getCode());
                //通知初始化失败
                return upLoadSliceFileDTO;
            } else {
                upLoadSliceFileDTO.setUpLoadFileStatus(UpLoadFileStageStatus.UPLOADED.getUpLoadStatus());
                upLoadSliceFileDTO.setStatus(UploadStatusEnums.UPLOAD_SECONDS.getCode());
                //通知可以秒传
                return upLoadSliceFileDTO;
            }
        }
        //todo 数据库中存在该文件，但是处于删除状态内 直接将数据库文件删除
        if (DEL.getFlag().equals(fileInfo.getUseFlag())) {
            int count = fileInfoMapper.deleteByPrimaryKey(fileId, userId);
            if (count == 0) {
                upLoadSliceFileDTO.setStatus(UploadStatusEnums.UPLOADING.getCode());
                upLoadSliceFileDTO.setUpLoadFileStatus(UpLoadFileStageStatus.FAIL.getUpLoadStatus());
                return upLoadSliceFileDTO;
            }
        }
        //其他状态，出现问题了熬，直接返回错误
        upLoadSliceFileDTO.setStatus(UploadStatusEnums.UPLOADING.getCode());
        upLoadSliceFileDTO.setUpLoadFileStatus(UpLoadFileStageStatus.FAIL.getUpLoadStatus());
        return upLoadSliceFileDTO;
    }

    private Boolean updateSpace(List<String> fileIds,String userId,int incOrDec){
        // todo 解决循环依赖，再开一个service？，直接拿bean
        UserService userService = applicationContext.getBean(UserService.class);
        List<FileInfo> fileInfoList = fileIds.stream()
                .map(fileId -> fileInfoMapper.selectByPrimaryKey(fileId, userId))
                .toList();
        return userService.UpdateUserSpace(fileInfoList, incOrDec);
    }




}
