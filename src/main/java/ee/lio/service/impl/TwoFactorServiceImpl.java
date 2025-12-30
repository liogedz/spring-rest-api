package ee.lio.service.impl;

import ee.lio.exceptions.InvalidVerificationCodeException;
import ee.lio.service.TwoFactorService;
import ee.lio.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TwoFactorServiceImpl implements TwoFactorService {

    private final Map<String, String> codeStore = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Random random = new Random();
    private final UserService userService;

    public TwoFactorServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String generateAndStoreCode(String identifier) {
        String code = String.format("%06d",
                random.nextInt(999999));
        codeStore.put(identifier,
                code);
        scheduler.schedule(() -> codeStore.remove(identifier),
                5,
                TimeUnit.MINUTES);
        return code;
    }

    @Override
    public void validateCode(String identifier,
                             String submittedCode) {

        userService.getUserByIdentifier(identifier);

        String storedCode = codeStore.get(identifier);
        if (storedCode == null) {
            throw new InvalidVerificationCodeException("Verification code expired or not found");
        }
        if (!storedCode.equals(submittedCode)) {
            throw new InvalidVerificationCodeException("Code does not match");
        }
    }


    @Override
    public void clearCode(String identifier) {
        codeStore.remove(identifier);
    }
}
