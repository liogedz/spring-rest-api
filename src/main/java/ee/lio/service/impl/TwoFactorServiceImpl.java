package ee.lio.service.impl;

import ee.lio.service.TwoFactorService;
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
    public boolean validateCode(String identifier,
                                String submittedCode) {
        String actualCode = codeStore.get(identifier);
        return submittedCode != null && submittedCode.equals(actualCode);
    }

    @Override
    public void clearCode(String identifier) {
        codeStore.remove(identifier);
    }
}
