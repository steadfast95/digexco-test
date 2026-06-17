package ru.home.digexco;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

//реализация банкомата в одном классе( исключительно для задачи) )
public interface ATM {
    Receipt input(Cash cash);

    Cash output(Application application);

    void init(BigDecimal amount);

    class AtmI implements ATM {
        private final AtomicReference<BigDecimal> atmAMount = new AtomicReference<>(BigDecimal.ZERO);

        @Override
        public Receipt input(Cash cash) {
            BigDecimal inputSum = new BigDecimal("0");
            for (Banknote banknote : cash.banknotes) {
                for (int n = 0; n < banknote.count; n++) {
                    inputSum = inputSum.add(banknote.type.nominal);
                }
            }

            atmAMount.set(atmAMount.get().add(inputSum));
            return new Receipt(cash.banknotes(), atmAMount.get());
        }

        @Override
        public Cash output(Application application) {
            BigDecimal amount = application.amount();

            Set<Banknote> banknotesList = new HashSet<>();

            List<BanknoteType> banknoteTypes = BanknoteType.sortedList();

            for (BanknoteType type : banknoteTypes) {
                int count = 0;
                while (amount.compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal subtract = amount.subtract(type.nominal);
                    if (subtract.signum() == -1) {
                        break;
                    }
                    amount = subtract;
                    count++;
                }
                banknotesList.add(new Banknote(type, count));

            }

            if (amount.compareTo(BigDecimal.ZERO) != 0) {
                throw new RuntimeException(String.format("Can't out cash with current application %s", application));
            }


            return new Cash(banknotesList);
        }

        @Override
        public void init(BigDecimal amount) {
            atmAMount.set(amount);
        }
    }

    record Receipt(Set<Banknote> banknotes, BigDecimal remainingFunds) {
    }

    record Cash(Set<Banknote> banknotes) {
    }

    record Application(BigDecimal amount) {
    }

    record Banknote(BanknoteType type, int count) {

    }

    enum BanknoteType {
        ten(BigDecimal.TEN),
        hundred(BigDecimal.valueOf(100)),
        thousand(BigDecimal.valueOf(1000)); //etc
        private BigDecimal nominal;

        private static final List<BanknoteType> sortedList = Arrays.stream(BanknoteType.values())
                .sorted(Comparator.comparing(bt -> bt.nominal, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        BanknoteType(BigDecimal nominal) {
            this.nominal = nominal;
        }

        public static List<BanknoteType> sortedList() {
            return sortedList;
        }
    }
}

