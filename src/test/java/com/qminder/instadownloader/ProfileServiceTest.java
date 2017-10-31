package com.qminder.instadownloader;

import com.qminder.instadownloader.domain.UserDetail;
import com.qminder.instadownloader.repository.UserRepository;
import com.qminder.instadownloader.service.ProfileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class,
        loader = AnnotationConfigContextLoader.class)
@DataJpaTest
public class ProfileServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileService profileService;

    @Test
    public void testFindByUserName() {
        UserDetail userDetail = new UserDetail();
        userDetail.setUserName("Test1");
        userDetail.setFileSavingDirectory("c://");
        userDetail.setLastDownloadedFileId("test1MaxId");
        userDetail.setFullName("Test1FullName");
        userRepository.saveAndFlush(userDetail);

        UserDetail savedUSerDetail = profileService.getUserDetailByName("Test1");
        assertEquals("Test1", userDetail.getUserName());
        assertEquals("c://", userDetail.getFileSavingDirectory());
        assertEquals("test1MaxId", userDetail.getLastDownloadedFileId());
        assertEquals("Test1FullName", userDetail.getFullName());
    }

    @Test
    public void testSaveUserDetail() {
        UserDetail userDetail = new UserDetail();
        userDetail.setUserName("Test2");
        userDetail.setFileSavingDirectory("c://");
        userDetail.setLastDownloadedFileId("test2MaxId");
        userDetail.setFullName("Test2FullName");
        UserDetail savedUserDetail1 = profileService.saveUserDetail(userDetail);

        assertEquals("Test2", savedUserDetail1.getUserName());
        assertEquals("c://", savedUserDetail1.getFileSavingDirectory());
        assertEquals("test2MaxId", savedUserDetail1.getLastDownloadedFileId());
        assertEquals("Test2FullName", savedUserDetail1.getFullName());
    }

    @Test
    public void testGetProfiles() {
        UserDetail userDetail1 = new UserDetail();
        userDetail1.setUserName("Test1");
        userDetail1.setFileSavingDirectory("c://");
        userDetail1.setLastDownloadedFileId("test1MaxId");
        userDetail1.setFullName("Test1FullName");
        userRepository.saveAndFlush(userDetail1);

        UserDetail userDetail2 = new UserDetail();
        userDetail2.setUserName("Test2");
        userDetail2.setFileSavingDirectory("c://");
        userDetail2.setLastDownloadedFileId("test2MaxId");
        userDetail2.setFullName("Test2FullName");
        userRepository.saveAndFlush(userDetail2);

        List<UserDetail> userDetails = profileService.getProfiles();
        assertTrue(isContain(userDetail1, userDetails));
        assertTrue(isContain(userDetail2, userDetails));

    }

    private boolean isContain(UserDetail userDetail, List<UserDetail> userDetails) {
        for (UserDetail savedUserDetail1 : userDetails) {
            if (savedUserDetail1.getUserName().equals(userDetail.getUserName()) &&
                    savedUserDetail1.getFullName().equals(userDetail.getFullName()) &&
                    savedUserDetail1.getFileSavingDirectory().equals(userDetail.getFileSavingDirectory()) &&
                    savedUserDetail1.getTotalFileDownloaded() == userDetail.getTotalFileDownloaded() &&
                    savedUserDetail1.getLastDownloadedFileId().equals(userDetail.getLastDownloadedFileId())) {
                return true;
            }
        }
        return false;
    }
}