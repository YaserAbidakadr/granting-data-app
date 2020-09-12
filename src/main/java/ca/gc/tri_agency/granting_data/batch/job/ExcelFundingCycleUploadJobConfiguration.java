package ca.gc.tri_agency.granting_data.batch.job;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import ca.gc.tri_agency.granting_data.batch.listener.ExcelItemProcessorListener;
import ca.gc.tri_agency.granting_data.batch.mapper.FundingCycleExcelRowMapper;
import ca.gc.tri_agency.granting_data.batch.pojo.FundingCycleExcelRow;
import ca.gc.tri_agency.granting_data.model.FiscalYear;
import ca.gc.tri_agency.granting_data.model.SystemFundingCycle;
import ca.gc.tri_agency.granting_data.model.SystemFundingOpportunity;
import ca.gc.tri_agency.granting_data.model.projection.FiscalYearProjection;
import ca.gc.tri_agency.granting_data.service.SystemFundingOpportunityService;

@Configuration
public class ExcelFundingCycleUploadJobConfiguration { // @formatter:off

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private SystemFundingOpportunityService sfoService;
	
	private List<SystemFundingOpportunity> extIdList = new ArrayList<>();
	
	private List<FiscalYearProjection> fyList = new ArrayList<>(); 
	
	private List<FundingCycleExcelRow> fcRowList = new ArrayList<>(250);
	
	private final String INSERT_SFC = "INSERT INTO SYSTEM_FUNDING_CYCLE VALUES (SEQ_SYSTEM_FUNDING_CYCLE.NEXTVAL,"
			+ " :fiscalYear, :extId, :numAppsReceived, :systemFundingOpportunity.id)";
	
	@Bean
	public Job excelFundingCycleUploadJob() throws Exception {
		return this.jobBuilderFactory.get("excelFundingCycleUploadJob")
				.start(loadAllExtIdsAndFYsStep())
				.next(processDataStep())
				.next(clearListsStep())
				.build();
	}
	
	@Bean
	public Step loadAllExtIdsAndFYsStep() throws Exception {
		return stepBuilderFactory.get("loadAllExtIdsAndFYsStep")
				.tasklet(new Tasklet() {
					
					@Override
					public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
						extIdList = sfoService.findAllSystemFundingOpportunities();
						
						return RepeatStatus.FINISHED;
					}
				})
				.build();
	}

	@Bean
	public Step processDataStep() throws Exception {
		return stepBuilderFactory.get("processDataStep")
				.<FundingCycleExcelRow, SystemFundingCycle>chunk(250)
				.reader(excelFileReader(""))
				.processor(excelFileSfcProcessor())
				.writer(sfcItemWriter())
				.listener(new ExcelItemProcessorListener())
				.build();

	}

	@Bean
	@StepScope	// any Bean that uses late-binding must be declared with scope="step"
	public PoiItemReader<FundingCycleExcelRow> excelFileReader(@Value("#{jobParameters['file.name']}") String excelFile) {
		PoiItemReader<FundingCycleExcelRow> reader = new PoiItemReader<>();
		
		reader.setLinesToSkip(1);
		reader.setResource(new FileSystemResource(excelFile));
		reader.setRowMapper(new FundingCycleExcelRowMapper());

		return reader;
	}
	
	@Bean
	public ItemProcessor<FundingCycleExcelRow, SystemFundingCycle> excelFileSfcProcessor() {
		return new ItemProcessor<FundingCycleExcelRow, SystemFundingCycle>() {

			@Override
			public SystemFundingCycle process(FundingCycleExcelRow item) throws Exception {
				SystemFundingOpportunity existingSfo = null;
				
				for (SystemFundingOpportunity sfo : extIdList) {
					if (sfo.getExtId().equals(item.getFoCycle())) {
						existingSfo = sfo;
						break;
					}
				}

				SystemFundingCycle sfc = new SystemFundingCycle();
				
				sfc.setExtId(item.getFoCycle());
				sfc.setFiscalYear(item.getCompetitionYear());
				sfc.setNumAppsReceived(item.getNumReceivedApps());
				sfc.setSystemFundingOpportunity(existingSfo);
				
				return existingSfo != null ? sfc : null;
			}
		};
	}
	
	@Bean
	public ItemProcessor<FundingCycleExcelRow, FiscalYear> excelFileFyProcessor() {
		return new ItemProcessor<FundingCycleExcelRow, FiscalYear>() {
			
			@Override
			public FiscalYear process(FundingCycleExcelRow item) throws Exception {
				for (FiscalYearProjection fy : fyList) {
					if (fy.getYear().equals(item.getCompetitionYear())) {
						return null;
					}
				}
				
				FiscalYear year = new FiscalYear();
				
				year.setYear(item.getCompetitionYear());
				
				return year;
			}
		};
	}
	
	@Bean
	public ItemWriter<SystemFundingCycle> sfcItemWriter() {
		return new JdbcBatchItemWriterBuilder<SystemFundingCycle>()
				.dataSource(dataSource)
				.sql(INSERT_SFC)
				.beanMapped()
				.build();
	}
	
	@Bean
	public Step clearListsStep() throws Exception {
		return stepBuilderFactory.get("clearListsStep")
				.tasklet(new Tasklet() {

					@Override
					public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
						fcRowList.clear();
						extIdList.clear();
						
						return RepeatStatus.FINISHED;
					}
				})
				.build();
	}


} // @formatter:on
