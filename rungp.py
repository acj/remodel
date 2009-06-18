#!/usr/bin/env python
#
# This script runs the GP on a set of models and stores the results of
# each run in a separate directory.

import os
import subprocess
import sys

# Parse CLI arguments
out_dir = ''

if len(sys.argv) < 2:
    print 'usage: ' + sys.argv[0] + ' <datestamp>'
    sys.exit(0)
else:
    out_dir = sys.argv[1]

models = ('AccountManagement-AbstractFactory', 'Balance-Builder', 'BankingCompany-AbstractFactory', 'BankingCompany-Builder', 'BankingCompany-Prototype', 'BankingCompany-Singleton', 'Bookstore-Composite', 'Bookstore-Singleton', 'BuildingCorporation-AbstractFactory', 'ChainStore-AbstractFactory', 'Clinic-Builder', 'Computer-Prototype', 'ComputerGame-Builder', 'ComputerStore-Builder', 'Consultation-Composite', 'DoctorOffice-Singleton', 'Drugstore-Prototype', 'Hospital-AbstractFactory', 'Hospital-Builder', 'Hospital-Composite', 'Hospital-Composite2', 'Hospital-Prototype', 'Hospital-Prototype2', 'Hospital-Prototype3', 'Hospital-Singleton', 'Hospital-Singleton2', 'HospitalWorker-Builder', 'Hotel-Composite', 'InsuranceCompany-Composite', 'Library-Builder', 'NZInternationalAirport', 'Patient-Singleton', 'Payment-AbstractFactory', 'PrimarySchool-Prototype', 'Professor-Prototype', 'PurchaseOrder-Composite', 'PurchaseOrder-Prototype', 'ReMoDD', 'Sale-Composite', 'School-Prototype', 'School-Singleton', 'SecondarySchool-Composite', 'Seminar-AbstractFactory', 'ServiceDepartment-Builder', 'ServiceDepartment', 'Shop-Composite', 'Shop-Prototype', 'SportClub-Singleton', 'StockExchange-Singleton', 'Student-Composite', 'SubjectArea-AbstractFactory', 'SubjectArea-AbstractFactory2', 'SubjectArea-Singleton', 'Supermarket-Prototype', 'TestModel', 'Ticketsales-Singleton', 'Tribunal-AbstractFactory', 'University-AbstractFactory', 'University-Builder', 'University-Builder2', 'University-Singleton', 'VideoShop-Builder', 'VideoShop-Composite')

seeds = ('1', '2', '3', '4', '5')

java_bin = '/usr/bin/java'
javacp = '.:ec/refactoring/jgrapht-jdk1.6.jar:nsuml1_4/lib/nsmdf.jar:nsuml1_4/build/nsuml1_3.jar:hsqldb/lib/hsqldb.jar:xerces-2_9_1/xercesImpl.jar:xerces-2_9_1/xml-apis.jar'
java_args = '-cp ' + javacp + ' -Xmx3000m -enableassertions ec.Evolve -file ec/refactoring/refactoring.params'

java_arg_tokens = java_args.split(' ')

for m in models:
    print 'Processing \'' + m + '\''

    # Write out params files for this model


    for s in seeds:
        print '\tSeed: ' + s
        param_file = open('ec/refactoring/refactoring.inputfile.params', 'w')
        param_file.write('ec.refactoring.inputfile = Models/' + m + '.xmi\n')
        param_file.write('seed.0 = ' + s + '\n')
        param_file.close()
        subprocess.call([java_bin] + java_arg_tokens, shell=False)

        try:
            # Results go in results/<date>/<model>/<seed>
            res_dir = 'results/' + out_dir + '/' + m + '/' + s
            os.makedirs(res_dir)
        except OSError:
            print 'Error making directories'
        finally:
            subprocess.call(['cp', '-R', 'out.stat', 'output', res_dir])

